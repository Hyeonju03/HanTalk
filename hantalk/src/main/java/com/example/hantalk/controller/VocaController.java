package com.example.hantalk.controller;

import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.service.VocaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/learning")
public class VocaController {

    private final VocaService vocaService;

    @GetMapping("/learning1")
    public String getFillBlank(@RequestParam(defaultValue = "5") int count, Model model) {
         List<VocaDTO> problems = vocaService.generateFillBlank(count);
         model.addAttribute("problems", problems);
         return "learning/learning1";
    }
}
