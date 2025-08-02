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
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/learning")
public class VocaController {

    private final VocaService vocaService;

    // 학습 1번 (단어 맞추기)
    @GetMapping("/learning1")
    public String getFillBlank(@RequestParam(defaultValue = "5") int count, Model model) {
         List<VocaDTO> problems = vocaService.getFillBlank(count);
         model.addAttribute("problems", problems);
         return "learning/learning1";
    }

    // 학습 3번 (4지선다 객관식)
    @GetMapping("/learning3")
    public String getMultipleChoice(@RequestParam(defaultValue = "5") int count, Model model) {
        List<Map<String, Object>> questions = vocaService.getMultipleChoice(count);
        model.addAttribute("questions", questions);
        return "learning/learning3";
    }
}
