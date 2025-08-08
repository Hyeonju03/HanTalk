package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.dto.VideoDTO;
import com.example.hantalk.entity.Video;
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/{role:admin|user}/video")  // /admin/video/** 또는 /user/video/**
public class VideoController {

    private final VideoService videoService;
    private static final String UPLOAD_PATH = System.getProperty("user.dir") + "/uploads/videos/";


    // ✅ 메인 페이지
    @GetMapping("/main")
    public String mainPage(@PathVariable String role) {
        return "video/" + role + "_main";  // admin_main.html 또는 user_main.html
    }

    // ✅ 목록 (권한별로 다르게 렌더링)
    @GetMapping("/list")
    public String list(@PathVariable String role,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "searchType", defaultValue = "title") String searchType,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {

        boolean isAdmin = "admin".equals(role);

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createDate"));

        // 검색 + 페이징 처리하는 부분
        Page<Video> videoPageEntity = videoService.getPagedVideos(keyword, searchType, pageable);

        // Entity -> DTO 변환
        Page<VideoDTO> videoPage = videoPageEntity.map(video -> {
            VideoDTO dto = new VideoDTO();
            dto.setVideoId(video.getVideoId());
            dto.setTitle(video.getTitle());
            dto.setContent(video.getContent());
            dto.setVideoName(video.getVideoName());
            dto.setCreateDate(video.getCreateDate());
            dto.setUpdateDate(video.getUpdateDate());
            return dto;
        });

        model.addAttribute("videoPage", videoPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isEmpty", videoPage.isEmpty());

        return "video/" + role + "_list";
    }

    // ✅ 상세 보기
    @GetMapping("/view/{id}")
    public String view(@PathVariable String role,
                       @PathVariable int id,
                       Model model) {
        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("video", video);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", "admin".equals(role));
        System.out.println("model role = " + role);
        return "video/" + role + "_view";
    }

    @PostMapping("/check-filename")
    @ResponseBody
    public boolean checkDuplicateFilename(@PathVariable String role,
                                          @RequestBody Map<String, String> payload) {
        String filename = payload.get("filename");
        return videoService.existsByFilename(filename);
    }

    @ResponseBody
    public boolean checkDuplicateFilename(@RequestBody Map<String, String> payload) {
        String filename = payload.get("filename");
        return videoService.existsByFilename(filename);
    }

    // ✅ 업로드 폼 (관리자 전용)
    @GetMapping("/upload")
    public String showUploadForm(@PathVariable String role, Model model) {
        if (!"admin".equals(role)) return "redirect:/user/video/list";

        model.addAttribute("video", new VideoDTO());
        model.addAttribute("role", role);
        return "video/upload";
    }

    // ✅ 업로드 처리
    @PostMapping("/upload")
    public String uploadVideo(@PathVariable String role,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("content") String content,
                              Model model) throws IOException {

        if (!"admin".equals(role)) return "redirect:/user/video/list";

        if (file.isEmpty()) {
            model.addAttribute("error", "파일이 비어있습니다.");
            return "video/upload";
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/videos/";
        String originalFilename = file.getOriginalFilename();

        // ✅ 서버에 이미 동일한 파일명이 존재하면 업로드 막기
        File checkFile = new File(uploadDir + originalFilename);
        if (checkFile.exists()) {
            model.addAttribute("error", "이미 동일한 영상이 업로드되어 있습니다.");
            return "video/upload";
        }

        // 중복 방지 저장
        String newFilename = getUniqueFileName(uploadDir, originalFilename);
        file.transferTo(new File(uploadDir, newFilename));

        VideoDTO dto = new VideoDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setVideoName(newFilename);

        int createdId = videoService.createVideo(dto);
        return "redirect:/admin/video/view/" + createdId;
    }

    // ✅ 수정 폼 (관리자)
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String role,
                           @PathVariable int id,
                           Model model) {
        if (!"admin".equals(role)) return "redirect:/user/video/list";

        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("video", video);
        model.addAttribute("role", role);
        return "video/edit";
    }

    // ✅ 수정 처리
    @PostMapping("/update")
    public String update(@PathVariable String role,
                         @RequestParam("videoId") int videoId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         @RequestParam(value = "page", required = false, defaultValue = "0") int page
    ) throws IOException {

        if (!"admin".equals(role)) return "redirect:/user/video/list";

        String uploadDir = System.getProperty("user.dir") + "/uploads/videos/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        VideoDTO existing = videoService.getVideo(videoId);
        String savedFilename = existing.getVideoName();

        // 새 파일이 업로드된 경우
        if (file != null && !file.isEmpty()) {
            // 기존 파일 삭제
            File oldFile = new File(uploadDir + savedFilename);
            if (oldFile.exists()) oldFile.delete();

            // 새 파일 저장 (같은 이름이면 덮어쓰기 가능)
            savedFilename = file.getOriginalFilename();

            File targetFile = new File(uploadDir + savedFilename);
            if (targetFile.exists()) {
                // 이름 겹치면 새 이름으로 저장
                savedFilename = getUniqueFileName(uploadDir, savedFilename);
            }

            file.transferTo(new File(uploadDir + savedFilename));
        }

        // DB 정보 업데이트
        VideoDTO dto = new VideoDTO();
        dto.setVideoId(videoId);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setVideoName(savedFilename);

        videoService.updateVideo(dto);
        return "redirect:/admin/video/list?page=" + page;
    }

    // ✅ Ajax 삭제 처리
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String role,
                                         @PathVariable int id) {
        if (!"admin".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음");
        }

        VideoDTO video = videoService.getVideo(id);
        String savedFilename = video.getVideoName();

        File file = new File(UPLOAD_PATH + savedFilename);
        if (file.exists()) file.delete();// 서버 저장소의 파일 삭제

        videoService.deleteVideo(id);
        return ResponseEntity.ok("삭제 성공");
    }

    // 중복 파일명 방지 함수
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
