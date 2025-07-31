package com.example.hantalk.controller;

import com.example.hantalk.dto.VideoDTO;
import com.example.hantalk.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    // 🔹 검색 기능 추가
    @GetMapping("/list")
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "searchType", defaultValue = "title") String searchType,
                       Model model) {
        List<VideoDTO> videos;

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                case "title":
                    videos = videoService.searchByTitle(keyword);
                    break;
                case "content":
                    videos = videoService.searchByContent(keyword);
                    break;
                case "all":
                    videos = videoService.searchByTitleOrContent(keyword);
                    break;
                default:
                    videos = videoService.getAllVideos(); // fallback
            }
        } else {
            videos = videoService.getAllVideos();
        }

        model.addAttribute("videos", videos);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        return "video/list";
    }



    // 영상 상세 보기
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") int id, Model model) {
        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("video", video);
        return "video/view";
    }

    // 영상 등록 폼
    @GetMapping("/upload")
    public String uploadForm() {
        return "video/upload";
    }

    // 영상 업로드 처리
    @PostMapping("/upload")
    public String uploadVideo(@RequestParam("file") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("content") String content,
                              HttpServletRequest request) throws IOException {

        String uploadDir = request.getServletContext().getRealPath("/upload/");
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
        return "redirect:/video/list";
    }

    // 수정 폼
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") int id, Model model) {
        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("video", video);
        return "video/edit";
    }

    // 수정 처리
    @PostMapping("/update")
    public String updateVideo(@RequestParam("videoId") int videoId,
                              @RequestParam("title") String title,
                              @RequestParam("content") String content,
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              HttpServletRequest request) throws IOException {

        String uploadDir = request.getServletContext().getRealPath("/upload/");
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        // 기존 정보 유지
        VideoDTO existing = videoService.getVideo(videoId);
        VideoDTO dto = new VideoDTO();
        dto.setVideoId(videoId);
        dto.setTitle(title);
        dto.setContent(content);

        if (file != null && !file.isEmpty()) {
            // 새 파일 업로드한 경우만 저장
            String originalFilename = file.getOriginalFilename();
            String savedFilename = getUniqueFileName(uploadDir, originalFilename);
            file.transferTo(new File(uploadDir, savedFilename));
            dto.setVideoName(savedFilename);
        } else {
            // 기존 파일 유지
            dto.setVideoName(existing.getVideoName());
        }

        videoService.updateVideo(dto);
        return "redirect:/video/view/" + dto.getVideoId();
    }

    // 기존 Get 방식 삭제 (화면에서 사용 시 필요)
    @GetMapping("/delete/{id}")
    public String deleteVideo(@PathVariable("id") int id) {
        videoService.deleteVideo(id);
        return "redirect:/video/list";
    }

    // Ajax 요청용 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideoAjax(@PathVariable("id") int id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 실패: " + e.getMessage());
        }
    }

    // 중복 파일명 방지 함수
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
