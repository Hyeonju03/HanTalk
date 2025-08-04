package com.example.hantalk.controller;

import com.example.hantalk.dto.ResourceDTO;
import com.example.hantalk.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;

    // 사용자 메인 페이지 + 검색 기능 포함
    @GetMapping("/main")
    public String userMainPage(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resourcePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            resourcePage = resourceService.searchResources(keyword.trim(), pageable);
        } else {
            resourcePage = resourceService.getAllResources(pageable);
        }

        model.addAttribute("resourcePage", resourcePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        return "resource/main";
    }

    // 관리자 목록 페이지 + 검색 기능 포함
    @GetMapping({"admin", "admin/main"})
    public String adminMainPage(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resourcePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            resourcePage = resourceService.searchResources(keyword.trim(), pageable);
        } else {
            resourcePage = resourceService.getAllResources(pageable);
        }

        model.addAttribute("resourcePage", resourcePage);
        model.addAttribute("keyword", keyword);
        return "resource/admin";
    }

    // 자료 삭제 (GET)
    @GetMapping("/delete/{id}")
    public String deleteResourceGet(@PathVariable("id") Long id) {
        resourceService.deleteResource(id.intValue());
        return "redirect:/resource/admin/main";
    }

    // 자료 삭제 (POST)
    @PostMapping("/delete/{id}")
    public String deleteResourcePost(@PathVariable int id, Model model) {
        resourceService.deleteResource(id);
        model.addAttribute("deletedId", id);
        return "resource/sakje";
    }

    // 관리자 등록 폼
    @GetMapping("/chuga")
    public String showCreateForm(Model model) {
        model.addAttribute("resourceDTO", new ResourceDTO());
        return "resource/chuga";
    }

    // 등록 처리 (파일 업로드)
    @PostMapping("/create")
    public String createResource(@ModelAttribute ResourceDTO resourceDTO,
                                 @RequestParam("file") MultipartFile file) {
        resourceService.createResourceWithFile(resourceDTO, file);
        return "redirect:/resource/admin/main";
    }

    // 업로드 폼
    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        model.addAttribute("resourceDTO", new ResourceDTO());
        return "resource/upload";
    }

    // 상세 보기
    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable int id, Model model) {
        ResourceDTO resource = resourceService.getResourceById(id);
        if (resource == null) {
            return "redirect:/resource/main";
        }
        model.addAttribute("resource", resource);
        return "resource/detail";
    }

    // 수정 폼
    @GetMapping("/sujung/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        ResourceDTO resource = resourceService.getResourceById(id);
        if (resource == null) {
            return "redirect:/resource/admin";
        }
        model.addAttribute("resourceDTO", resource);
        return "resource/sujung";
    }

    // 수정 처리
    @PostMapping("/edit/{id}")
    public String updateResource(@PathVariable int id,
                                 @ModelAttribute ResourceDTO resourceDTO,
                                 @RequestParam("file") MultipartFile file) {
        resourceService.updateResourceWithFile(id, resourceDTO, file);
        return "redirect:/resource/detail/" + id;
    }

    // 사용자 파일 다운로드
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        Path path = Paths.get("C:/aaa/HanTalk/hantalk/upload/" + fileName);
        UrlResource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        // DB에서 원본 파일명 조회
        String originalFileName = resourceService.getOriginalFileName(fileName);

        if (originalFileName == null || originalFileName.isEmpty()) {
            originalFileName = fileName;
        }

        String encodedFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);

        String contentDisposition = "attachment; filename=\"" + originalFileName + "\"; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}