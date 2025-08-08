package com.example.hantalk.controller;

import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.service.Inc_NoteService;
import com.example.hantalk.service.Learning_LogService;
import com.example.hantalk.service.VocaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class VocaController {

    private final VocaService vocaService;
    private final Learning_LogService learningLogService;
    private final Inc_NoteService incNoteService;

    String folderName = "study";

    // 학습 공간 페이지
    @GetMapping("/main")
    public String studyMain() {
        return "study/main";
    }

    // 문장 목록
    @GetMapping("/vocaList")
    public String vocaList(Model model) {
        List<VocaDTO> dtoList = vocaService.getSelectAll();
        model.addAttribute("dtoList", dtoList);
        return folderName + "/vocaList";
    }

    @GetMapping("/vocaList/admin")
    public String vocaAdminList(Model model) {
        List<VocaDTO> dtoList = vocaService.getSelectAll();
        model.addAttribute("dtoList", dtoList);
        return folderName + "/vocaAdmin";
    }

    @GetMapping("/vocaInsert")
    public String vocaInsert() {
        return folderName + "/vocaChuga";
    }

    @PostMapping("/vocaInsertProc")
    public String vocaInsertProc(VocaDTO vocaDTO) {
        vocaService.setInsert(vocaDTO);
        return "redirect:/study/vocaList/admin";
    }

    @GetMapping("/vocaUpdate/{id}")
    public String vocaUpdate(Model model, @PathVariable("id") int id) {
        VocaDTO searchDTO = new VocaDTO();
        searchDTO.setVocaId(id);
        VocaDTO dto = vocaService.getSelectOne(searchDTO);
        model.addAttribute("dto", dto);
        return folderName + "/vocaSujung";
    }

    @PostMapping("/vocaUpdateProc")
    public String vocaUpdateProc(VocaDTO vocaDTO) {
        vocaService.setUpdate(vocaDTO);
        return "redirect:/study/vocaList/admin";
    }


    // 학습 1번 (단어 맞추기)
    @GetMapping("/lesson1")
    public String getFillBlank(HttpSession session, Model model) {

        List<Integer> solvedIds = (List<Integer>) session.getAttribute("solvedIds_lesson1");
        if (solvedIds == null) solvedIds = new ArrayList<>();

        List<VocaDTO> vocaDTOList = vocaService.getFillBlank(solvedIds, 1);

        List<Map<String, Object>> problems = new ArrayList<>();
        for (VocaDTO dto : vocaDTOList) {
            Map<String, Object> map = new HashMap<>();
            map.put("vocaId", dto.getVocaId());
            map.put("vocabulary", dto.getVocabulary());
            map.put("description", dto.getDescription());
            problems.add(map);

            solvedIds.add(dto.getVocaId());
        }

        session.setAttribute("solvedIds_lesson1", solvedIds);
        model.addAttribute("problems", problems);

        return "study/lesson1";
    }

    //  학습 1번 처리 (정답 체크 + 오답노트 + 로그 기록)
    @PostMapping("/lesson1/check")
    @ResponseBody
    public boolean checkLesson1Answer(@RequestParam int vocaId,
                                      @RequestParam String answer,
                                      @RequestParam String correctAnswer,
                                      HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        Integer userNo = (Integer) session.getAttribute("userNo");
        boolean isCorrect = answer.equals(correctAnswer);

        if (userId != null) {
            if (isCorrect) {
                incNoteService.deleteIncorrectNote(userNo, vocaId, null);
            } else {
                incNoteService.saveIncorrectNote(userNo, vocaId, null);
            }

            learningLogService.updateLearning_Log(userId, 1);
        }

        return isCorrect;
    }

    // 학습 3번 (4지선다 객관식)
    @GetMapping("/lesson3")
    public String getMultipleChoice(HttpSession session, Model model) {
        List<Integer> solvedIds = (List<Integer>) session.getAttribute("solvedIds_lesson3");
        if (solvedIds == null) solvedIds = new ArrayList<>();

        // 중복 제거된 문제 1개 가져오기
        List<Map<String, Object>> questions = vocaService.getMultipleChoice(solvedIds, 1);

        for (Map<String, Object> question : questions) {
            Integer vocaId = (Integer) question.get("vocaId");
            solvedIds.add(vocaId);
        }

        session.setAttribute("solvedIds_lesson3", solvedIds);
        model.addAttribute("questions", questions);

        return "study/lesson3";
    }

    //  학습 3번 처리 (정답 체크 + 오답노트 + 로그 기록)
    @PostMapping("/lesson3/check")
    @ResponseBody
    public boolean checkLesson3Answer(@RequestParam int vocaId,
                                      @RequestParam String answer,
                                      @RequestParam String correctAnswer,
                                      HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        Integer userNo = (Integer) session.getAttribute("userNo");
        boolean isCorrect = answer.equals(correctAnswer);

        if (userId != null && userNo != null) {
            if (isCorrect) {
                incNoteService.deleteIncorrectNote(userNo, vocaId, null);
            } else {
                incNoteService.saveIncorrectNote(userNo, vocaId, null);
            }

            learningLogService.updateLearning_Log(userId, 3);
        }

        return isCorrect;
    }

    // 이전에 푼 문제 목록을 초기화
    @GetMapping("/reset")
    public String resetSession(HttpSession session) {
        session.removeAttribute("solvedIds_lesson1");
        session.removeAttribute("solvedIds_lesson3");
        return "redirect:/study/lesson1";
    }
}
