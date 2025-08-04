package com.example.hantalk.controller;

import com.example.hantalk.dto.ResourceDTO;
import com.example.hantalk.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public String listPage(Model model) {
        model.addAttribute("resourceList", resourceService.getAllResources());
        return "resource/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("resource", new ResourceDTO());
        return "resource/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute ResourceDTO dto) {
        resourceService.createResource(dto);
        return "redirect:/resources";
    }

    @GetMapping("/{id}")
    public String detailPage(@PathVariable Long id, Model model) {
        model.addAttribute("resource", resourceService.getResourceById(id.intValue())); // ✅ 수정
        return "resource/detail";
    }

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("resource", resourceService.getResourceById(id.intValue())); // ✅ 수정
        return "resource/update";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute ResourceDTO dto) {
        resourceService.updateResource(id.intValue(), dto); // ✅ 수정
        return "redirect:/resources/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        resourceService.deleteResource(id.intValue()); // ✅ 수정
        return "redirect:/resources";
    }
}
