package com.example.hantalk.controller;

import com.example.hantalk.dto.ResourceDTO;
import com.example.hantalk.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;

    // ✅ 사용자 메인 페이지
    // http://localhost:9001/resource/main
    @GetMapping("/main")
    public String userMainPage(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resourcePage = resourceService.getAllResources(pageable);
        model.addAttribute("resourcePage", resourcePage);
        model.addAttribute("currentPage", page);
        return "resource/main"; // templates/resource/main.html
    }

    // 관리자 목록 페이지 (메인)
    //http://localhost:9001/resource/admin
    @GetMapping({"admin"})  // /resource/admin 또는 /resource/admin/main 둘 다 처리
    public String adminMainPage(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resourcePage = resourceService.getAllResources(pageable);
        model.addAttribute("resourcePage", resourcePage);
        return "resource/admin";  // 관리자 목록 뷰 (adminMain.html)
    }

    // 자료 삭제 처리
    @GetMapping("/delete/{id}")
    public String deleteResource(@PathVariable("id") Long id) {
        resourceService.deleteResource(id.intValue());
        return "redirect:/resource/admin/main";
    }

    // ✅ 관리자용 자료 등록 폼 (chuga.html)
    @GetMapping("/chuga")
    public String showCreateForm(Model model) {
        model.addAttribute("resourceDTO", new ResourceDTO());
        return "resource/chuga";
    }

    // ✅ 자료 등록 처리
    @PostMapping("/create")
    public String createResource(@ModelAttribute ResourceDTO resourceDTO) {
        resourceService.createResource(resourceDTO);
        return "redirect:/resource/admin";
    }

    // ✅ 사용자 및 관리자 공통 자료 상세 보기 (상세에서 삭제/수정 이동 가능)
    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable int id, Model model) {
        ResourceDTO resource = resourceService.getResourceById(id);
        if (resource == null) {
            return "redirect:/resource/list";
        }
        model.addAttribute("resource", resource);
        return "resource/detail";
    }

    // ✅ 관리자용 수정 폼 (sujung.html)
    @GetMapping("/sujung/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        ResourceDTO resource = resourceService.getResourceById(id);
        if (resource == null) {
            return "redirect:/resource/admin";
        }
        model.addAttribute("resourceDTO", resource);
        return "resource/sujung";
    }

    // ✅ 자료 수정 처리
    @PostMapping("/edit/{id}")
    public String updateResource(@PathVariable int id,
                                 @ModelAttribute ResourceDTO resourceDTO) {
        resourceService.updateResource(id, resourceDTO);
        return "redirect:/resource/detail/" + id;
    }

    // ✅ 삭제 처리 → sakje.html 페이지로 리디렉션
    @PostMapping("/sakje/{id}")
    public String deleteResource(@PathVariable int id, Model model) {
        resourceService.deleteResource(id);
        model.addAttribute("deletedId", id);
        return "resource/sakje"; // 삭제 완료 안내 뷰
    }
}
