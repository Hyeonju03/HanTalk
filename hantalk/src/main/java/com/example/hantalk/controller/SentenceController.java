package com.example.hantalk.controller;

import com.example.hantalk.dto.SentenceDTO;
import com.example.hantalk.entity.Sentence;
import com.example.hantalk.service.Inc_NoteService;
import com.example.hantalk.service.Learning_LogService;
import com.example.hantalk.service.SentenceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
    private final Inc_NoteService incNoteService;

    String folderName = "study";

    // 문장 목록
    @GetMapping("/sentenceList")
    public String sentenceList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            Model model) {

        Page<SentenceDTO> paging = sentenceService.getSelectAll(page, kw);
        model.addAttribute("paging", paging); // model에 "paging"이라는 이름으로 객체를 담음
        model.addAttribute("kw", kw);
        return folderName + "/sentenceList";
    }

    @GetMapping("/sentenceList/admin")
    public String sentenceAdminList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            Model model) {

        Page<SentenceDTO> paging = sentenceService.getSelectAll(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return folderName + "/sentenceAdmin";
    }

    @GetMapping("/sentenceInsert")
    public String sentenceInsert() {
        return folderName + "/sentenceChuga";
    }

    @PostMapping("/sentenceInsertProc")
    public String sentenceInsertProc(SentenceDTO sentenceDTO) {
        sentenceService.setInsert(sentenceDTO);
        return "redirect:/study/sentenceList/admin";
    }

    @GetMapping("/sentenceUpdate/{id}")
    public String sentenceUpdate(Model model, @PathVariable("id") int id,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "kw", defaultValue = "") String kw) {

        SentenceDTO searchDTO = new SentenceDTO();
        searchDTO.setSentenceId(id);
        SentenceDTO dto = sentenceService.getSelectOne(searchDTO);
        model.addAttribute("dto", dto);
        model.addAttribute("page", page); // 페이지 번호를 모델에 추가
        model.addAttribute("kw", kw);   // 검색어를 모델에 추가
        return folderName + "/sentenceSujung";
    }

    @PostMapping("/sentenceUpdateProc")
    public String sentenceUpdateProc(SentenceDTO sentenceDTO,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "kw", defaultValue = "") String kw) {
        try {
            sentenceService.setUpdate(sentenceDTO);
            return String.format("redirect:/study/sentenceList/admin?page=%d&kw=%s", page, kw);
        } catch (Exception e) {
            e.printStackTrace(); // 예외가 발생하면 콘솔에 출력
            // 오류 페이지로 리다이렉트하거나, 현재 페이지로 돌아가 오류 메시지를 표시하는 로직을 추가할 수 있습니다.
            return "redirect:/error"; // 임시 오류 페이지로 리다이렉트
        }
    }

    @GetMapping("/sentenceDelete/{id}")
    public String sentenceDelete(@PathVariable int id,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "kw", defaultValue = "") String kw) {

        SentenceDTO dto = new SentenceDTO();
        dto.setSentenceId(id);
        sentenceService.setDelete(dto);

        if ("null".equals(kw)) {
            kw = "";
        }

        return String.format("redirect:/study/sentenceList/admin?page=%d&kw=%s", page, kw);
    }

    @GetMapping("/lesson2")
    public String lesson2() {
        return folderName + "/lesson2";
    }

    @GetMapping("/api/get-lesson2-sentence")
    @ResponseBody
    public Map<String, Object> getLesson2Sentence(HttpSession session) {
        Sentence selected = sentenceService.getSelectRandomEntity();  // ✅ 문장 객체 전체를 한 번만 가져옴

        if (selected == null) {
            return Collections.singletonMap("error", "문장을 불러올 수 없습니다.");
        }

        // 세션에 원본 문장과 오답 횟수 저장
        session.setAttribute("originalSentence", selected.getMunjang()); // ✅ selected 객체에서 문장을 가져옴
        session.setAttribute("originalSentenceId", selected.getSentenceId());   // ✅ selected 객체에서 sentenceId 저장
        session.setAttribute("incorrectCount", 0);

        // 문장을 ^ 기준으로 분리하고 섞기
        String[] splitList = selected.getMunjang().split("\\^"); // ✅ selected 객체에서 문장을 가져옴
        List<String> shuffleList = Arrays.asList(splitList);
        Collections.shuffle(shuffleList);

        Map<String, Object> response = new HashMap<>();
        response.put("pieces", shuffleList); // 섞인 조각들만 클라이언트에 전달
        response.put("sentenceId", selected.getSentenceId());   // 응답에 포함

        return response;
    }

    @PostMapping("/api/check-lesson2-answer")
    @ResponseBody
    public Map<String, Object> checkLesson2Answer(@RequestBody Map<String, List<String>> payload, HttpSession session) {
        String originalSentenceWithCaret = (String) session.getAttribute("originalSentence");
        Integer sentenceId = (Integer) session.getAttribute("originalSentenceId");
        int incorrectCount = (int) session.getAttribute("incorrectCount");
        List<String> userPieces = payload.get("userPieces");

        if (originalSentenceWithCaret == null) {
            return Collections.singletonMap("error", "문제가 로드되지 않았거나 세션이 만료되었습니다.");
        }

        String correctSentence = originalSentenceWithCaret.replace("^", " ");
        String submittedSentence = String.join(" ", userPieces);

        boolean isCorrect = correctSentence.equals(submittedSentence);

        Map<String, Object> response = new HashMap<>();
        response.put("isCorrect", isCorrect);
        response.put("correctAnswer", originalSentenceWithCaret);
        response.put("sentenceId", sentenceId);

        if (isCorrect) {
            Integer userNo = (Integer) session.getAttribute("userNo");
            if(userNo!= null && sentenceId != null) {
                // 정답일 경우, 오답노트에 해당 항목이 있으면 삭제
                incNoteService.deleteIncorrectNote(userNo, null, sentenceId);
            }

            String userId = (String) session.getAttribute("userId");
            if(userId != null) {
                learningLogService.updateLearning_Log(userId, 2);
            }

            Sentence nextSelected = sentenceService.getSelectRandomEntity();
            String nextSentence = nextSelected.getMunjang();

            // 다음 문제의 정보를 세션에 정확히 업데이트
            session.setAttribute("originalSentence", nextSentence);
            session.setAttribute("originalSentenceId", nextSelected.getSentenceId());
            session.setAttribute("incorrectCount", 0);

            List<String> nextPieces = Arrays.asList(nextSentence.split("\\^"));
            Collections.shuffle(nextPieces);

            response.put("message", "정답입니다! 다음 문제로 넘어갑니다.");
            response.put("nextPieces", nextPieces);
            response.put("nextSentenceId", nextSelected.getSentenceId());

        } else {
            incorrectCount++;
            session.setAttribute("incorrectCount", incorrectCount);

            if (incorrectCount >= 3) {
                // 3번 이상 틀렸을 경우, 오답노트에 저장
                Integer userNo = (Integer) session.getAttribute("userNo");
                if (userNo != null && sentenceId != null) {
                    incNoteService.saveIncorrectNote(userNo, null, sentenceId);
                }

                // 다음 문제 로드
                Sentence nextSelected = sentenceService.getSelectRandomEntity();
                String nextSentence = nextSelected.getMunjang();

                // 다음 문제의 정보를 세션에 정확히 업데이트
                session.setAttribute("originalSentence", nextSentence);
                session.setAttribute("originalSentenceId", nextSelected.getSentenceId());
                session.setAttribute("incorrectCount", 0);

                List<String> nextPieces = Arrays.asList(nextSentence.split("\\^"));
                Collections.shuffle(nextPieces);

                response.put("message", "3번 틀렸습니다. 다음 문제로 넘어갑니다.");
                response.put("nextPieces", nextPieces);
                response.put("nextSentenceId", nextSelected.getSentenceId());
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
