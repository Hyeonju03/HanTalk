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
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/ResourceFile";

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }

    private boolean isImage(String fileName) {
        return getExtension(fileName).matches("png|jpg|jpeg|gif|bmp|webp");
    }

    private boolean isTextFile(String fileName) {
        return getExtension(fileName).matches("txt|csv|log|md|java|xml|json|html|css|js");
    }

    // 사용자 메인 페이지
    @GetMapping({"/main", "/list"})
    public String userMainPage(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resourcePage = (keyword != null && !keyword.trim().isEmpty())
                ? resourceService.searchResources(keyword.trim(), pageable)
                : resourceService.getAllResources(pageable);

        model.addAttribute("resourcePage", resourcePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        return "resource/main";
    }

    // 관리자 메인 페이지
    @GetMapping({"/admin", "/admin/main"})
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
        model.addAttribute("currentPage", page);
        return "resource/admin";
    }

    // 등록 폼
    @GetMapping("/chuga")
    public String showCreateForm(Model model) {
        model.addAttribute("resourceDTO", new ResourceDTO());
        return "resource/chuga";
    }

    // 등록 처리
    @PostMapping("/create")
    public String createResource(@ModelAttribute ResourceDTO resourceDTO,
                                 @RequestParam("file") MultipartFile file) {
        resourceService.createResourceWithFile(resourceDTO, file);
        return "redirect:/resource/admin/main";
    }

    // 상세 보기
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
        if (resource == null) return "redirect:/resource/admin/main";
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

    // 삭제 처리
    @GetMapping("/delete/{id}")
    public String deleteResource(@PathVariable int id) {
        resourceService.deleteResource(id);
        return "redirect:/resource/admin/main";
    }

    // 파일 다운로드
    @GetMapping("/download/file/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        if (fileName.contains("..")) return ResponseEntity.badRequest().build();

        Path path = Paths.get(uploadDir).resolve(fileName).normalize();
        UrlResource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String originalFileName = resourceService.getOriginalFileName(fileName);
        if (originalFileName == null) originalFileName = fileName;

        String encodedFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + originalFileName + "\"; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // 사용자 파일 미리보기
    @GetMapping("/preview/{fileName}")
    public String previewFile(@PathVariable String fileName, Model model, Principal principal) {
        return handleFilePreview(fileName, model, principal, false);
    }

    // 관리자 파일 미리보기
    @GetMapping("/admin/preview/{fileName}")
    public String adminPreviewFile(@PathVariable String fileName, Model model, Principal principal) {
        return handleFilePreview(fileName, model, principal, true);
    }

    // 파일 미리보기 공통 처리
    private String handleFilePreview(String fileName, Model model, Principal principal, boolean isAdminPath) {
        try {
            if (fileName.contains("..")) {
                return isAdminPath ? "redirect:/resource/admin" : "redirect:/resource/main";
            }

            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            if (!Files.exists(filePath)) {
                return isAdminPath ? "redirect:/resource/admin" : "redirect:/resource/main";
            }

            String ext = getExtension(fileName);
            model.addAttribute("fileName", fileName);
            model.addAttribute("isImage", isImage(fileName));
            model.addAttribute("isDocument", ext.matches("pdf|xls|xlsx|hwp|doc|docx|ppt|pptx"));
            model.addAttribute("isText", isTextFile(fileName));
            model.addAttribute("isOfficeFile", ext.matches("ppt|pptx|doc|docx|xls|xlsx"));

            boolean isAdmin = principal != null && principal.getName().toLowerCase().startsWith("admin");
            model.addAttribute("isAdmin", isAdmin);

            if (isTextFile(fileName)) {
                try {
                    String text = Files.readString(filePath, StandardCharsets.UTF_8);
                    model.addAttribute("textContent", text);
                } catch (IOException e) {
                    model.addAttribute("textContent", "텍스트 파일을 읽는 중 오류가 발생했습니다.");
                }
            }

            return isAdminPath ? "resource/adminpreview" : "resource/preview";

        } catch (Exception e) {
            e.printStackTrace();
            return isAdminPath ? "redirect:/resource/admin" : "redirect:/resource/main";
        }
    }

    // 이미지 출력
    @GetMapping("/image/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName) throws IOException {
        Path path = Paths.get(uploadDir).resolve(fileName).normalize();
        UrlResource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/png"))
                .body(resource);
    }
}
