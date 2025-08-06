package com.example.hantalk.controller;

import com.example.hantalk.dto.SentenceDTO;
import com.example.hantalk.entity.Learning_Log;
import com.example.hantalk.entity.Sentence;
import com.example.hantalk.service.Learning_LogService;
import com.example.hantalk.service.SentenceService;
import com.example.hantalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/study")
@Controller
public class SentenceController {
    private static final Logger logger = LoggerFactory.getLogger(SentenceController.class);
    private final SentenceService sentenceService;
    private final Learning_LogService learningLogService;

    String folderName = "study";

    // 문장 목록
    @GetMapping("/sentenceList")
    public String sentenceList(Model model) {
        List<SentenceDTO> dtoList = sentenceService.getSelectAll();
        model.addAttribute("dtoList", dtoList);
        return folderName + "/sentenceList";
    }

    @GetMapping("/sentenceList/admin")
    public String sentenceAdminList(Model model) {
        List<SentenceDTO> dtoList = sentenceService.getSelectAll();
        model.addAttribute("dtoList", dtoList);
        return folderName + "/sentenceAdmin";
    }

    @GetMapping("/sentenceUpdate")
    public String sentenceUpdate(Model model) {
        List<SentenceDTO> dtoList = sentenceService.getSelectAll();
        model.addAttribute("dtoList", dtoList);
        return folderName + "/sentenceSujung";
    }

    @GetMapping("/lesson2")
    public String lesson2() {
        return folderName + "/lesson2";
    }

    @GetMapping("/api/get-lesson2-sentence")
    @ResponseBody
    public Map<String, Object> getLesson2Sentence(HttpSession session) {
        String sentence = sentenceService.getSelectRandom().getMunjang();

        if (sentence == null) {
            return Collections.singletonMap("error", "문장을 불러올 수 없습니다.");
        }

        // 세션에 원본 문장과 오답 횟수 저장
        session.setAttribute("originalSentence", sentence);
        session.setAttribute("incorrectCount", 0);

        // 문장을 ^ 기준으로 분리하고 섞기
        String[] splitList = sentence.split("\\^");
        List<String> shuffleList = Arrays.asList(splitList);
        Collections.shuffle(shuffleList);

        Map<String, Object> response = new HashMap<>();
        response.put("pieces", shuffleList); // 섞인 조각들만 클라이언트에 전달

        return response;
    }

    @PostMapping("/api/check-lesson2-answer")
    @ResponseBody
    public Map<String, Object> checkLesson2Answer(@RequestBody Map<String, List<String>> payload, HttpSession session) {
        String originalSentenceWithCaret = (String) session.getAttribute("originalSentence");
        int incorrectCount = (int) session.getAttribute("incorrectCount");
        List<String> userPieces = payload.get("userPieces");

        if (originalSentenceWithCaret == null) {
            // 세션이 만료되었거나 문제가 로드되지 않았을 경우
            return Collections.singletonMap("error", "문제가 로드되지 않았거나 세션이 만료되었습니다.");
        }

        String correctSentence = originalSentenceWithCaret.replace("^", " ");
        String submittedSentence = String.join(" ", userPieces);

        boolean isCorrect = correctSentence.equals(submittedSentence);

        Map<String, Object> response = new HashMap<>();
        response.put("isCorrect", isCorrect);

        if (isCorrect) {
            // 정답일 경우

            String userId = (String) session.getAttribute("userId");
            if(userId != null) {
                learningLogService.updateLearning_Log(userId, 2);
            }

            String nextSentence = sentenceService.getSelectRandom().getMunjang();
            session.setAttribute("originalSentence", nextSentence);
            session.setAttribute("incorrectCount", 0);

            List<String> nextPieces = Arrays.asList(nextSentence.split("\\^"));
            Collections.shuffle(nextPieces);

            response.put("message", "정답입니다! 다음 문제로 넘어갑니다.");
            response.put("nextPieces", nextPieces);

        } else {
            // 오답일 경우, 오답 횟수 증가
            incorrectCount++;
            session.setAttribute("incorrectCount", incorrectCount);

            if (incorrectCount >= 3) {
                // 3번 이상 틀렸을 경우, 다음 문제 로드
                String nextSentence = sentenceService.getSelectRandom().getMunjang();
                session.setAttribute("originalSentence", nextSentence);
                session.setAttribute("incorrectCount", 0);

                List<String> nextPieces = Arrays.asList(nextSentence.split("\\^"));
                Collections.shuffle(nextPieces);

                response.put("message", "3번 틀렸습니다. 다음 문제로 넘어갑니다.");
                response.put("nextPieces", nextPieces);

            } else {
                response.put("message", "오답입니다. 다시 시도하세요.");
            }
        }

        return response;
    }

    @GetMapping("/lesson4")
    public String lesson4() {
        return folderName + "/lesson4";
    }

    @GetMapping("/api/get-lesson4-sentence")
    @ResponseBody
    public Map<String, String> getLesson4Sentence() {
        String sentence = sentenceService.getSelectRandom().getMunjang();

        if (sentence != null) {
            sentence = sentence.replace("^", " ");
        } else {
            return Collections.singletonMap("sentence", "문장을 불러올 수 없습니다.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("sentence", sentence);
        return response;
    }

    @PostMapping("/api/check-lesson4-answer")
    @ResponseBody
    public Map<String, Object> checkLesson4Answer(@RequestBody Map<String, String> payload, HttpSession session) {
        String originalSentence = payload.get("originalSentence");
        String userAnswer = payload.get("userAnswer");

        boolean isCorrect = originalSentence.equals(userAnswer);

        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            if(isCorrect) {
                learningLogService.updateLearning_Log(userId, 4);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("isCorrect", isCorrect);

        return response;
    }
}
