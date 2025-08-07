package com.example.hantalk.controller;

import com.example.hantalk.dto.SentenceDTO;
import com.example.hantalk.entity.Learning_Log;
import com.example.hantalk.entity.Sentence;
import com.example.hantalk.service.Inc_NoteService;
import com.example.hantalk.service.Learning_LogService;
import com.example.hantalk.service.SentenceService;
import com.example.hantalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/study")
@Controller
public class SentenceController {
    private static final Logger logger = LoggerFactory.getLogger(SentenceController.class);
    private final SentenceService sentenceService;
    private final UserService userService;
    private final Learning_LogService learningLogService;
    private final Inc_NoteService incNoteService;

    String folderName = "study";

    @GetMapping("/lesson2")
    public String lesson2() {
        return folderName + "/lesson2";
    }

    // SentenceController.java
    @GetMapping("/api/get-lesson2-sentence")
    @ResponseBody
    public Map<String, Object> getLesson2Sentence(HttpSession session) {
        Sentence selected = sentenceService.getSelectRandomEntity();  // ✅ 문장 객체 전체
        String sentence = sentenceService.getSelectRandom().getMunjang();

        if (sentence == null) {
            return Collections.singletonMap("error", "문장을 불러올 수 없습니다.");
        }

        // 세션에 원본 문장과 오답 횟수 저장
        session.setAttribute("originalSentence", sentence);
        session.setAttribute("originalSentenceId", selected.getSentenceId());   // ✅ sentenceId 저장
        session.setAttribute("incorrectCount", 0);

        // 문장을 ^ 기준으로 분리하고 섞기
        String[] splitList = sentence.split("\\^");
        List<String> shuffleList = Arrays.asList(splitList);
        Collections.shuffle(shuffleList);

        Map<String, Object> response = new HashMap<>();
        response.put("pieces", shuffleList); // 섞인 조각들만 클라이언트에 전달
        response.put("sentenceId", selected.getSentenceId());   // 응답에 포함

        return response;
    }

    // SentenceController.java
    @PostMapping("/api/check-lesson2-answer")
    @ResponseBody
    public Map<String, Object> checkLesson2Answer(@RequestBody Map<String, List<String>> payload, HttpSession session) {
        String originalSentenceWithCaret = (String) session.getAttribute("originalSentence");
        Integer sentenceId = (Integer) session.getAttribute("originalSentenceId");  // ✅ sentenceId 가져오기
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
        response.put("correctAnswer", originalSentenceWithCaret);  // ✅ 오답노트 저장용
        response.put("sentenceId", sentenceId);                    // ✅ 오답노트 저장용

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

    //받아쓰기
    @GetMapping("/lesson4")
    public String lesson4() {
        return folderName + "/lesson4";
    }

    //random문장 가져오기
    @GetMapping("/api/get-lesson4-sentence")
    @ResponseBody
    public Map<String, String> getLesson4Sentence(HttpSession session) { // session 받아오기
        Sentence selected = sentenceService.getSelectRandomEntity();  // ✅ sentenceId도 필요하므로 Entity로 가져옴 (수정)
        String sentence = selected.getMunjang();

        if (sentence != null) {
            session.setAttribute("originalSentence", sentence); // ✅ 세션에 저장
            session.setAttribute("originalSentenceId", selected.getSentenceId()); // ✅ 세션에 sentenceId도 저장
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
        Integer userNo = (Integer) session.getAttribute("userNo"); // ✅ 오답노트 저장용

        // ✅ 오답노트용 값 불러오기
        String rawSentence = (String) session.getAttribute("originalSentence"); // ✅ ^ 포함 원본
        Integer sentenceId = (Integer) session.getAttribute("originalSentenceId");

        // ✅ 정답이면 학습 로그 업데이트
        if (userId != null && isCorrect) {
            learningLogService.updateLearning_Log(userId, 4);
        }

        // ✅ 오답이면 오답노트 저장
        if (!isCorrect && userNo != null && sentenceId != null) {
            incNoteService.saveIncorrectNote(userNo, null, sentenceId); // 단어는 null
        }

        Map<String, Object> response = new HashMap<>();
        response.put("isCorrect", isCorrect);

        return response;
    }
}
