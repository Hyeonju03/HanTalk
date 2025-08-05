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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;

    // 실제 업로드 파일 저장 위치
    private final String uploadDir = "C:/aaa/HanTalk/hantalk/ResourceFile/";

    // 확장자 추출 메서드
    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private boolean isImage(String fileName) {
        if (fileName == null) return false;
        String ext = getExtension(fileName);
        return ext.matches("png|jpg|jpeg|gif|bmp|webp");
    }

    private boolean isPdf(String fileName) {
        if (fileName == null) return false;
        String ext = getExtension(fileName);
        return ext.equals("pdf");
    }

    private boolean isOfficeFile(String fileName) {
        if (fileName == null) return false;
        String ext = getExtension(fileName);
        return ext.matches("ppt|pptx|doc|docx|xls|xlsx");
    }

    private boolean isTextFile(String fileName) {
        if (fileName == null) return false;
        String ext = getExtension(fileName);
        return ext.matches("txt|csv|log|md|java|xml|json|html|css|js");
    }

    // 사용자용 메인 페이지 (미리보기 가능 여부 세팅 제거하고 항상 true로 세팅)
    @GetMapping("/main")
    public String userMainPage(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resourcePage = (keyword != null && !keyword.trim().isEmpty())
                ? resourceService.searchResources(keyword.trim(), pageable)
                : resourceService.getAllResources(pageable);

        // 미리보기 가능 여부 세팅 제거 or 무조건 true로 세팅
        resourcePage.forEach(resource -> resource.setPreviewAvailable(true));

        model.addAttribute("resourcePage", resourcePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        return "resource/main";
    }

    // 관리자용 메인 페이지
    @GetMapping({"admin", "admin/main"})
    public String adminMainPage(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resourcePage = (keyword != null && !keyword.trim().isEmpty())
                ? resourceService.searchResources(keyword.trim(), pageable)
                : resourceService.getAllResources(pageable);

        model.addAttribute("resourcePage", resourcePage);
        model.addAttribute("keyword", keyword);
        return "resource/admin";
    }

    // 자료 등록 폼
    @GetMapping("/chuga")
    public String showCreateForm(Model model) {
        model.addAttribute("resourceDTO", new ResourceDTO());
        return "resource/chuga";
    }

    // 자료 등록 처리
    @PostMapping("/create")
    public String createResource(@ModelAttribute ResourceDTO resourceDTO,
                                 @RequestParam("file") MultipartFile file) {
        resourceService.createResourceWithFile(resourceDTO, file);
        return "redirect:/resource/admin/main";
    }

    // 자료 상세 페이지
    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable int id, Model model) {
        ResourceDTO resource = resourceService.getResourceById(id);
        if (resource == null) return "redirect:/resource/main";
        model.addAttribute("resource", resource);
        return "resource/detail";
    }

    // 수정 폼
    @GetMapping("/sujung/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        ResourceDTO resource = resourceService.getResourceById(id);
        if (resource == null) return "redirect:/resource/admin";
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

    // 삭제 처리 (GET)
    @GetMapping("/delete/{id}")
    public String deleteResourceGet(@PathVariable("id") Long id) {
        resourceService.deleteResource(id.intValue());
        return "redirect:/resource/admin/main";
    }

    // 삭제 처리 (POST)
    @PostMapping("/delete/{id}")
    public String deleteResourcePost(@PathVariable int id, Model model) {
        resourceService.deleteResource(id);
        model.addAttribute("deletedId", id);
        return "resource/sakje";
    }

    // 파일 다운로드 : attachment 헤더로 강제 다운로드
    @GetMapping("/download/file/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        Path path = Paths.get(uploadDir).resolve(fileName);
        UrlResource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String originalFileName = resourceService.getOriginalFileName(fileName);
        if (originalFileName == null || originalFileName.isEmpty()) {
            originalFileName = fileName;
        }

        String encodedFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + originalFileName + "\"; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // 파일 보기 : inline 헤더로 브라우저 내에서 열기 (가능하면 미리보기)
    @GetMapping("/view/file/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        UrlResource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }

    // 파일 미리보기 전용 페이지 (텍스트 포함, 외부 뷰어 iframe 처리)
    @GetMapping("/preview/{fileName}")
    public String previewFile(@PathVariable String fileName, Model model) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        if (!Files.exists(filePath)) {
            return "redirect:/resource/admin/main";
        }

        String ext = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) ext = fileName.substring(i + 1).toLowerCase();

        boolean isImage = ext.matches("png|jpg|jpeg|gif|bmp|webp");
        boolean isDocument = ext.matches("pdf|xls|xlsx|hwp|doc|docx|ppt|pptx");
        boolean isText = ext.matches("txt|csv|log|md|java|xml|json|html|css|js");
        boolean isOfficeFile = ext.matches("ppt|pptx|doc|docx|xls|xlsx");

        model.addAttribute("fileName", fileName);
        model.addAttribute("isImage", isImage);
        model.addAttribute("isDocument", isDocument);
        model.addAttribute("isText", isText);
        model.addAttribute("isOfficeFile", isOfficeFile);

        if (isText) {
            String text = Files.readString(filePath, StandardCharsets.UTF_8);
            model.addAttribute("textContent", text);
        }

        return "resource/preview";
    }
}
