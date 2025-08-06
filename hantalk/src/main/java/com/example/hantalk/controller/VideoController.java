package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.dto.VideoDTO;
import com.example.hantalk.entity.Video;
import com.example.hantalk.repository.VideoRepository;
import com.example.hantalk.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;


    // ------------------------------
    // 👤 사용자 기능 (/video/**)
    // ------------------------------

    @GetMapping("/video/list")
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "searchType", defaultValue = "all") String searchType,
                       Model model,
                       HttpSession session) {

        // 📌 isAdmin 여부 확인
        boolean isAdmin = false;
        Object isAdminAttr = session.getAttribute("isAdmin");
        if (isAdminAttr instanceof Boolean) {
            isAdmin = (Boolean) isAdminAttr;
        }
        model.addAttribute("isAdmin", isAdmin);

        Pageable pageable = PageRequest.of(page, size, Sort.by("videoId").descending());
        Page<Video> videoPage = videoService.getPagedVideos(keyword, searchType, pageable);

        model.addAttribute("videoPage", videoPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("currentPage", page);
        model.addAttribute("startNumber", page * size + 1); // 번호 표시용

        return "video/list";
    }

    @GetMapping("/video/view/{id}")
    public String view(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userId") == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
            return "redirect:/video/list";
        }

        VideoDTO video = videoService.getVideo(id);
        String encodedFileName = URLEncoder.encode(video.getVideoName(), StandardCharsets.UTF_8);
        video.setVideoName(encodedFileName);
        model.addAttribute("video", video);

        // 🔐 관리자 여부를 직접 체크해서 model에 넣기
        boolean isAdmin = false;
        Object isAdminAttr = session.getAttribute("isAdmin");
        if (isAdminAttr instanceof Boolean) {
            isAdmin = (Boolean) isAdminAttr;
        }
        model.addAttribute("isAdmin", isAdmin);

        return "video/view";
    }

    // ------------------------------
    // 📁 관리자 기능 (/admin/video/**)
    // ------------------------------
    @GetMapping("/admin")
    public String adminHome() {
        return "video/admin"; // → templates/video/admin.html 로 연결
    }

    @GetMapping("/admin/video/list")
    public String adminList(@RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "searchType", defaultValue = "title") String searchType,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            Model model,
                            HttpSession session) {

        // 관리자 여부 확인
        boolean isAdmin = false;
        Object isAdminAttr = session.getAttribute("isAdmin");
        if (isAdminAttr instanceof Boolean) {
            isAdmin = (Boolean) isAdminAttr;
        }
        model.addAttribute("isAdmin", isAdmin);

        Pageable pageable = PageRequest.of(page, size, Sort.by("videoId").descending());
        Page<Video> videoPage = videoService.getPagedVideos(keyword, searchType, pageable);

        model.addAttribute("videoPage", videoPage); // ✔ Page 객체 전달
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("currentPage", page);
        model.addAttribute("startNumber", page * size + 1);

        return "video/list"; // 사용자와 같은 템플릿 사용 가능
    }

    @GetMapping("/admin/video/view/{id}")
    public String adminView(@PathVariable("id") int id, Model model) {
        return prepareView(id, model);
    }

    @GetMapping("/admin/video/upload")
    public String uploadForm(Model model, HttpSession session) {
        // 📌 isAdmin 여부 확인
        boolean isAdmin = false;
        Object isAdminAttr = session.getAttribute("isAdmin");
        if (isAdminAttr instanceof Boolean) {
            isAdmin = (Boolean) isAdminAttr;
        }
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("video", new VideoDTO());
        return "video/upload";
    }

    @PostMapping("/admin/video/upload")
    public String uploadVideo(@RequestParam("file") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("content") String content) throws IOException {
        String uploadDir = System.getProperty("user.dir") + "/uploads/videos";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String originalFilename = file.getOriginalFilename();
        String savedFilename = getUniqueFileName(uploadDir, originalFilename);
        file.transferTo(new File(uploadDir, savedFilename));

        VideoDTO dto = new VideoDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setVideoName(savedFilename);
        videoService.createVideo(dto);

        return "redirect:/admin/video/list";
    }

    @GetMapping("/admin/video/edit/{id}")
    public String editForm(@PathVariable int id, Model model, HttpSession session) {
        // 📌 isAdmin 여부 확인
        boolean isAdmin = false;
        Object isAdminAttr = session.getAttribute("isAdmin");
        if (isAdminAttr instanceof Boolean) {
            isAdmin = (Boolean) isAdminAttr;
        }
        model.addAttribute("isAdmin", isAdmin);

        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("video", video);

        return "video/edit";
    }

    @PostMapping("/admin/video/update")
    public String updateVideo(@RequestParam("videoId") int videoId,
                              @RequestParam("title") String title,
                              @RequestParam("content") String content,
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              HttpServletRequest request) throws IOException {

        String uploadDir = System.getProperty("user.dir") + "/uploads/videos";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        VideoDTO existing = videoService.getVideo(videoId);
        VideoDTO dto = new VideoDTO();
        dto.setVideoId(videoId);
        dto.setTitle(title);
        dto.setContent(content);

        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String savedFilename = getUniqueFileName(uploadDir, originalFilename);
            file.transferTo(new File(uploadDir, savedFilename));
            dto.setVideoName(savedFilename);
        } else {
            dto.setVideoName(existing.getVideoName());
        }

        videoService.updateVideo(dto);
        return "redirect:/admin/video/view/" + dto.getVideoId();
    }

    @GetMapping("/admin/video/delete/{id}")
    public String deleteVideo(@PathVariable("id") int id) {
        videoService.deleteVideo(id);
        return "redirect:/admin/video/list";
    }

    @DeleteMapping("/admin/video/{id}")
    public ResponseEntity<String> deleteVideoAjax(@PathVariable("id") int id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 실패: " + e.getMessage());
        }
    }

    // ------------------------------
    // 🛠️ 내부 재사용 메서드
    // ------------------------------

    private List<VideoDTO> getSearchedVideos(String keyword, String searchType) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return switch (searchType) {
                case "title" -> videoService.searchByTitle(keyword);
                case "content" -> videoService.searchByContent(keyword);
                case "all" -> videoService.searchByTitleOrContent(keyword);
                default -> videoService.getAllVideos();
            };
        } else {
            return videoService.getAllVideos();
        }
    }

    private String prepareView(int id, Model model) {
        VideoDTO video = videoService.getVideo(id);
        String encodedFileName = URLEncoder.encode(video.getVideoName(), StandardCharsets.UTF_8);
        video.setVideoName(encodedFileName);
        model.addAttribute("video", video);
        return "video/view";
    }

    private String getUniqueFileName(String uploadDir, String originalFilename) {
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String extension = FilenameUtils.getExtension(originalFilename);
        baseName = baseName.replaceAll("[^a-zA-Z0-9가-힣_\\-]", "_");

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
