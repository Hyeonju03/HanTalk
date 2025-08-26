package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.dto.VideoDTO;
import com.example.hantalk.service.VideoService;
import com.example.hantalk.dto.FavoriteVideoDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private static final String UPLOAD_PATH = System.getProperty("user.dir") + "/uploads/videos/";

    @PostMapping("/admin/check-filename")
    @ResponseBody
    public boolean checkFilename(@RequestBody Map<String, String> request, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session) || !SessionUtil.hasRole(session, "ADMIN")) {
            return false;
        }
        String filename = request.get("filename");
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        File file = new File(UPLOAD_PATH + filename);
        return file.exists();
    }

    @GetMapping("/admin/main")
    public String adminMain(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        String role = SessionUtil.getRole(session);
        if (!"ADMIN".equals(role)) {
            return "redirect:/video/list";
        }
        model.addAttribute("role", role);
        return "video/main";
    }

    @GetMapping("/list")
    public Object list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "searchType", defaultValue = "title") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "isFavorite", defaultValue = "false") boolean isFavorite, // 찜한 영상 목록 요청 여부
            HttpSession session,
            Model model,
            HttpServletRequest request) {

        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        String role = SessionUtil.getRole(session);
        if (role == null || !("USER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role))) {
            return "redirect:/login";
        }

        Integer userId = SessionUtil.getLoginUserNo(session);
        if (userId == null) {
            return "redirect:/login";
        }

        Page<VideoDTO> videoPage;

        // 찜한 영상 전체 목록을 요청한 경우
        if (isFavorite) {
            List<VideoDTO> favoriteVideos = videoService.getFavoriteVideos(userId);
            // List를 Page 객체로 변환 (페이지네이션 기능을 이용하기 위함)
            Pageable pageable = PageRequest.of(page, 9, Sort.by(Sort.Direction.DESC, "createDate"));
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), favoriteVideos.size());
            Page<VideoDTO> favoritePage = new PageImpl<>(favoriteVideos.subList(start, end), pageable, favoriteVideos.size());
            videoPage = favoritePage;
            model.addAttribute("isFavorite", true);
        } else {
            // 일반 영상 목록을 요청한 경우 (기존 로직 유지)
            if (keyword == null) {
                keyword = "";
            }
            Pageable pageable = PageRequest.of(page, 9, Sort.by(Sort.Direction.DESC, "createDate"));
            videoPage = videoService.getPagedVideos(keyword, searchType, pageable);
            model.addAttribute("isFavorite", false);
        }

        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {
            return new ResponseEntity<>(videoPage, HttpStatus.OK);
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        model.addAttribute("videoPage", videoPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isEmpty", videoPage.isEmpty());
        model.addAttribute("userId", userId);
        return "video/list";
    }

    @GetMapping("/contentView/{id}")
    public String userView(@PathVariable int id, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        String role = SessionUtil.getRole(session);
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        VideoDTO video = videoService.getVideoAndIncrementView(id);
        model.addAttribute("video", video);
        model.addAttribute("role", SessionUtil.getRole(session));
        model.addAttribute("isAdmin", SessionUtil.hasRole(session, "ADMIN"));
        return "video/contentView";
    }

    @GetMapping("/admin/list")
    public String adminList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "searchType", defaultValue = "title") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        if (!SessionUtil.isLoggedIn(session) || !SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/login";
        }
        if (keyword == null) {
            keyword = "";
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<VideoDTO> videoPage = videoService.getPagedVideos(keyword, searchType, pageable);

        model.addAttribute("videoPage", videoPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("role", "ADMIN");
        model.addAttribute("isEmpty", videoPage.isEmpty());
        return "video/admin";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable int id, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        String role = SessionUtil.getRole(session);
        if (role == null || !("USER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role))) {
            return "redirect:/login";
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        VideoDTO video = videoService.getVideoAndIncrementView(id);
        model.addAttribute("video", video);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", isAdmin);
        return "video/view";
    }

    @GetMapping("/admin/chuga")
    public String adminUploadForm(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session) || !SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/login";
        }
        model.addAttribute("video", new VideoDTO());
        model.addAttribute("role", SessionUtil.getRole(session));
        return "video/chuga";
    }

    @PostMapping("/admin/chugaProc")
    public String adminUploadProc(
            HttpSession session,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            Model model) throws IOException {

        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/video/list";
        }

        if (file.isEmpty()) {
            model.addAttribute("error", "파일이 비어있습니다.");
            model.addAttribute("role", SessionUtil.getRole(session));
            return "video/chuga";
        }

        String uploadDir = UPLOAD_PATH;
        String originalFilename = file.getOriginalFilename();
        File existingFile = new File(uploadDir + originalFilename);
        if (existingFile.exists()) {
            model.addAttribute("error", "이미 동일한 영상 파일명이 존재합니다. 다른 이름으로 변경하세요.");
            model.addAttribute("role", SessionUtil.getRole(session));
            return "video/chuga";
        }

        file.transferTo(new File(uploadDir, originalFilename));
        VideoDTO dto = new VideoDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setVideoName(originalFilename);

        int createdId = videoService.createVideo(dto);
        return "redirect:/video/view/" + createdId;
    }

    @GetMapping("/admin/sujung/{id}")
    public String adminEditForm(@PathVariable int id, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session) || !SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/login";
        }
        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("video", video);
        model.addAttribute("role", SessionUtil.getRole(session));
        return "video/sujung";
    }

    @PostMapping("/admin/sujungProc")
    public String adminUpdateProc(
            HttpSession session,
            @RequestParam("videoId") int videoId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "page", defaultValue = "0") int page) throws IOException {

        if (!SessionUtil.isLoggedIn(session) || !SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/login";
        }
        String uploadDir = UPLOAD_PATH;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        VideoDTO existing = videoService.getVideo(videoId);
        String savedFilename = existing.getVideoName();

        if (file != null && !file.isEmpty()) {
            File oldFile = new File(uploadDir + savedFilename);
            if (oldFile.exists()) oldFile.delete();

            String originalFilename = file.getOriginalFilename();
            savedFilename = getUniqueFileName(uploadDir, originalFilename);
            file.transferTo(new File(uploadDir, savedFilename));
        }

        VideoDTO dto = new VideoDTO();
        dto.setVideoId(videoId);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setVideoName(savedFilename);
        videoService.updateVideo(dto);
        return "redirect:/video/admin/list?page=" + page;
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> adminDelete(@PathVariable int id, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session) || !SessionUtil.hasRole(session, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }
        VideoDTO video = videoService.getVideo(id);
        String savedFilename = video.getVideoName();

        File file = new File(UPLOAD_PATH + savedFilename);
        if (file.exists()) file.delete();

        videoService.deleteVideo(id);
        return ResponseEntity.ok("삭제 성공");
    }

    private String getUniqueFileName(String uploadDir, String originalFilename) {
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String extension = FilenameUtils.getExtension(originalFilename);
        baseName = baseName.replaceAll("[^a-zA-Z0-9가-힣_\\-()]", "_");
        String newFilename = baseName + "." + extension;
        int count = 1;
        File file = new File(uploadDir, newFilename);
        while (file.exists()) {
            newFilename = baseName + "(" + count + ")." + extension;
            file = new File(uploadDir, newFilename);
            count++;
        }
        return newFilename;
    }

    @GetMapping("/favorites")
    @ResponseBody
    public ResponseEntity<List<VideoDTO>> favoriteVideos(HttpSession session) {
        if (!SessionUtil.isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer userId = SessionUtil.getLoginUserNo(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<VideoDTO> favoriteVideos = videoService.getFavoriteVideos(userId);
        return ResponseEntity.ok(favoriteVideos);
    }

    @PostMapping("/favorite")
    @ResponseBody
    public ResponseEntity<Void> addFavorite(@RequestBody FavoriteVideoDto favoriteDto, HttpSession session) {
        Integer sessionUserId = SessionUtil.getLoginUserNo(session);
        if (!SessionUtil.isLoggedIn(session) || sessionUserId == null || !sessionUserId.equals(favoriteDto.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        videoService.addFavoriteVideo(favoriteDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/favorite")
    @ResponseBody
    public ResponseEntity<Void> removeFavorite(@RequestBody FavoriteVideoDto favoriteDto, HttpSession session) {
        Integer sessionUserId = SessionUtil.getLoginUserNo(session);
        if (!SessionUtil.isLoggedIn(session) || sessionUserId == null || !sessionUserId.equals(favoriteDto.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        videoService.removeFavoriteVideo(favoriteDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorite/check/{videoId}")
    @ResponseBody
    public ResponseEntity<Boolean> isFavorite(@PathVariable int videoId, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        Integer userId = SessionUtil.getLoginUserNo(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        boolean isFavorite = videoService.isVideoFavorite(userId, videoId);
        return ResponseEntity.ok(isFavorite);
    }
}
