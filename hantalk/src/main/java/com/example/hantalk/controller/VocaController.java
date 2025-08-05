package com.example.hantalk.controller;

import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.service.Learning_LogService;
import com.example.hantalk.service.UserService;
import com.example.hantalk.service.VocaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class VocaController {

    private final VocaService vocaService;
    private final Learning_LogService learningLogService;

    // 학습 공간 페잊지
    @GetMapping("/main")
    public String studyMain() {
        return "study/main";
    }

    // 학습 1번 (단어 맞추기)
    @GetMapping("/lesson1")
    public String getFillBlank(@RequestParam(defaultValue = "5") int count, Model model) {
         List<VocaDTO> problems = vocaService.getFillBlank(count);
         model.addAttribute("problems", problems);
         return "study/lesson1";
    }

    // 학습 3번 (4지선다 객관식)
    @GetMapping("/lesson3")
    public String getMultipleChoice(@RequestParam(defaultValue = "5") int count, Model model) {
        List<Map<String, Object>> questions = vocaService.getMultipleChoice(count);
        model.addAttribute("questions", questions);
        return "study/lesson3";
    }

    @PostMapping("/complete")
    @ResponseBody
    public String completeLesson(@RequestParam int lessonNo, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            learningLogService.setLearningLog(userId);
            return "success";
        }
        return "fail";
    }
}
