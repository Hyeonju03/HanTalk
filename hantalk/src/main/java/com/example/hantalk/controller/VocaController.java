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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class VocaController {

    private final VocaService vocaService;
    private final UserService userService;
    private final Learning_LogService learningLogService;

    // 학습 공간 페잊지
    @GetMapping("/main")
    public String studyMain() {
        return "study/main";
    }

    // 학습 1번 (단어 맞추기)
    @GetMapping("/lesson1")
    public String getFillBlank(@RequestParam(defaultValue = "5") int count, HttpSession session, Model model) {
        // 이미 푼 단어 ID 세션에서 가져오기
        List<Integer> solvedIds = (List<Integer>) session.getAttribute("solvedIds_lesson1");
        if (solvedIds == null) solvedIds = new ArrayList<>();

        // 단어 5개 뽑기
        List<VocaDTO> vocaDTOList = vocaService.getFillBlank(solvedIds, count);

        // Map으로 변환해서 필요한 값만 넘김
        List<Map<String, Object>> problems = new ArrayList<>();
        for (VocaDTO dto : vocaDTOList) {
            Map<String, Object> map = new HashMap<>();
            map.put("vocaId", dto.getVocaId());
            map.put("vocabulary", dto.getVocabulary());
            map.put("description", dto.getDescription());
            // createDate는 필요 없으면 안 넣어도 됨
            problems.add(map);

            // solvedIds 업데이트
            solvedIds.add(dto.getVocaId());
        }

        session.setAttribute("solvedIds_lesson1", solvedIds);
        model.addAttribute("problems", problems);

        return "study/lesson1";
    }


    // 학습 1번 완료 처리
    @PostMapping("/complete1")
    @ResponseBody
    public String completeLesson1(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        System.out.println("### completeLesson1 호출: userId=" + userId);

        if (userId != null) {
            learningLogService.updateLearning_Log(userId, 1);
            return "success";
        }
        return "fail";
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

    // 학습 3번 완료 처리
    @PostMapping("/complete3")
    @ResponseBody
    public String completeLesson3(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        System.out.println("### completeLesson3 호출: userId=" + userId);

        if (userId != null) {
            learningLogService.updateLearning_Log(userId, 3);
            return "success";
        }
        return "fail";
    }

    @GetMapping("/reset")
    public String resetSession(HttpSession session) {
        session.removeAttribute("solvedIds_lesson1");
        session.removeAttribute("solvedIds_lesson3");
        return "redirect:/study/lesson1";
    }

}
