package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.dto.VideoDTO;
import com.example.hantalk.service.VideoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
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

    // 관리자 메인 페이지 (변동 없음)
    @GetMapping("/admin/main")
    public String adminMain(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        String role = SessionUtil.getRole(session);
        if (!"ADMIN".equals(role)) {
            return "redirect:/video/list"; // 통합된 목록 페이지로 리다이렉트
        }
        model.addAttribute("role", role);
        return "video/main";
    }

    // 영상 목록 통합 (사용자 & 관리자)
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "searchType", defaultValue = "title") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        String role = SessionUtil.getRole(session);
        if (role == null || !("USER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role))) {
            return "redirect:/login";
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<VideoDTO> videoPage = videoService.searchVideos(keyword, searchType, pageable);

        model.addAttribute("videoPage", videoPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", isAdmin); // isAdmin 모델 속성을 추가
        model.addAttribute("isEmpty", videoPage.isEmpty());

        return "video/list"; // 'list.html' 단일 뷰 반환
    }


    /* =========================
       사용자 영상 상세
       - 관리자면 관리자 뷰로 리다이렉트
    ========================== */
    @GetMapping("/contentView/{id}")
    public String userView(@PathVariable int id, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

/*        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");*/

        String role = SessionUtil.getRole(session);
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("video", video);
        model.addAttribute("role", SessionUtil.getRole(session));
        model.addAttribute("isAdmin", SessionUtil.hasRole(session, "ADMIN"));  // 이 부분 추가
   /*     model.addAttribute("isAdmin", isAdmin);*/

/*        if (isAdmin) { // 관리자면 관리자 상세 페이지로
            return "redirect:/video/admin/view/" + id;
        }*/
        return "video/contentView";
    }

    /* =========================
       관리자 영상 목록
    ========================== */
    @GetMapping("/admin/list")
    public String adminList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "searchType", defaultValue = "title") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        // ✅ 관리자만 접근 가능하도록 역할 확인
        if (!SessionUtil.isLoggedIn(session) || !SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/login";
        }

        if (!SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/video/contentList";
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<VideoDTO> videoPage = videoService.searchVideos(keyword, searchType, pageable);

        model.addAttribute("videoPage", videoPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("role", "ADMIN");
        model.addAttribute("isEmpty", videoPage.isEmpty());

        return "video/admin"; // 'admin.html' 뷰 반환
    }

    // 영상 상세 통합 (사용자 & 관리자)
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

        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("video", video);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", isAdmin); // isAdmin 모델 속성을 추가

        return "video/view"; // 'view.html' 단일 뷰 반환
    }

    // 관리자 영상 업로드 폼 (URL 유지)
    @GetMapping("/admin/chuga")
    public String adminUploadForm(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session) || !SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/login";
        }
        if (!SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/video/contentList";
        }

        model.addAttribute("video", new VideoDTO());
        model.addAttribute("role", SessionUtil.getRole(session));
        return "video/chuga";
    }

    // 관리자 영상 업로드 처리 (URL 유지)
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

    // 관리자 영상 수정 폼 (URL 유지)
    @GetMapping("/admin/sujung/{id}")
    public String adminEditForm(@PathVariable int id, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session) || !SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/login";
        }

        if (!SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/video/contentList";
        }

        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("video", video);
        model.addAttribute("role", SessionUtil.getRole(session));
        return "video/sujung";
    }

    // 관리자 영상 수정 처리 (URL 유지)
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
        if (!SessionUtil.hasRole(session, "ADMIN")) {
            return "redirect:/video/contentList";
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
        return "redirect:/video/admin/list?page=" + page; // 목록 페이지 URL 통일
    }

    // 관리자 영상 삭제 (URL 유지)
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
}