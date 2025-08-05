package com.example.hantalk.controller;

import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.service.UserService;
import com.example.hantalk.service.VocaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class VocaController {

    private final VocaService vocaService;
    private final UserService userService;

    // 학습 공간 페잊지
    @GetMapping("/main")
    public String studyMain() {
        return "study/main";
    }

    // 학습 1번 (단어 맞추기)
    @GetMapping("/lesson1")
    public String getFillBlank(@RequestParam(defaultValue = "5") int count, HttpSession session, Model model) {
        List<Integer> solvedIds = (List<Integer>) session.getAttribute("solvedIds_lesson1");
        if (solvedIds == null) solvedIds = new ArrayList<>();

        List<VocaDTO> problems = vocaService.getFillBlank(solvedIds, count);

        for (VocaDTO dto : problems) {
            solvedIds.add(dto.getVocaId());
        }
        session.setAttribute("solvedIds_lesson1", solvedIds);

         model.addAttribute("problems", problems);
         return "study/lesson1";
    }

    // 학습 3번 (4지선다 객관식)
    @GetMapping("/lesson3")
    public String getMultipleChoice(@RequestParam(defaultValue = "5") int count, HttpSession session, Model model) {
        List<Integer> solvedIds = (List<Integer>) session.getAttribute("solvedIds_lesson3");
        if (solvedIds == null) solvedIds = new ArrayList<>();

        List<Map<String, Object>> questions = vocaService.getMultipleChoice(solvedIds, count);

        for (Map<String, Object> question : questions) {
            Integer vocaId = (Integer) question.get("vocaId");
            solvedIds.add(vocaId);
        }

        session.setAttribute("solvedIds_lesson3", solvedIds);

        model.addAttribute("questions", questions);
        return "study/lesson3";
    }

    @GetMapping("/reset")
    public String resetSession(HttpSession session) {
        session.removeAttribute("solvedIds_lesson1");
        session.removeAttribute("solvedIds_lesson3");
        return "redirect:/study/lesson1";
    }

    @PostMapping("/complete")
    @ResponseBody
    public String completeLesson(@RequestParam int lessonNo, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            userService.setLeaningLog(userId, lessonNo);
            return "success";
        }
        return "fail";
    }
}
