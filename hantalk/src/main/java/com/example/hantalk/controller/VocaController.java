package com.example.hantalk.controller;

import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.service.Inc_NoteService;
import com.example.hantalk.service.Learning_LogService;
import com.example.hantalk.service.VocaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // 게임학습 & 한글 테스트 선택 페이지
    @GetMapping("/contact")
    public String contactPage() {
        return folderName + "/contact";
    }

    // 문장 목록
    @GetMapping("/vocaList")
    public String vocaList(Model model,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "kw", defaultValue = "") String kw) {

        Page<VocaDTO> paging = vocaService.getSelectAll(page, kw);

        // 페이지 구조
        int totalPages  = paging.getTotalPages();
        int currentPage = page;
        int window = 10;

        // 블록 계산
        int currentBlock = currentPage / window;
        int start = currentBlock * window;
        int end   = Math.min(start + window - 1, totalPages - 1);

        // 이전/다음 블록 여부와 이동 시작 페이지
        boolean hasPrevBlock = start > 0;
        boolean hasNextBlock = end < totalPages - 1;
        int prevBlockPage = Math.max(0, start - window);
        int nextBlockPage = end + 1;

        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("hasPrevBlock", hasPrevBlock);
        model.addAttribute("hasNextBlock", hasNextBlock);
        model.addAttribute("prevBlockPage", prevBlockPage);
        model.addAttribute("nextBlockPage", nextBlockPage);

        return folderName + "/vocaList";
    }

    @GetMapping("/vocaList/admin")
    public String vocaAdminList(Model model,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<VocaDTO> paging = vocaService.getSelectAll(page, kw);

        int totalPages  = paging.getTotalPages();
        int currentPage = page;
        int window = 10;

        int currentBlock = currentPage / window;
        int start = currentBlock * window;
        int end   = Math.min(start + window - 1, totalPages - 1);

        boolean hasPrevBlock = start > 0;
        boolean hasNextBlock = end < totalPages - 1;
        int prevBlockPage = Math.max(0, start - window);
        int nextBlockPage = end + 1;

        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("hasPrevBlock", hasPrevBlock);
        model.addAttribute("hasNextBlock", hasNextBlock);
        model.addAttribute("prevBlockPage", prevBlockPage);
        model.addAttribute("nextBlockPage", nextBlockPage);

        return folderName + "/vocaAdmin";
    }

    @GetMapping("/vocaInsert")
    public String vocaInsert() {
        return folderName + "/vocaChuga";
    }

    @PostMapping("/vocaInsertProc")
    public String vocaInsertProc(VocaDTO vocaDTO, RedirectAttributes ra) {
        String vocab = vocaDTO.getVocabulary();
        String desc  = vocaDTO.getDescription();

        // 단어: 공백/빈 값 금지
        if (vocab == null || vocab.trim().isEmpty()) {
            ra.addFlashAttribute("error", "단어를 입력하세요.");
            // 필요 시 폼 값 유지
            ra.addFlashAttribute("dto", vocaDTO);
            return "redirect:/study/vocaInsert";
        }
        // 의미: 완전 빈 값 금지(공백은 허용)
        if (desc == null || desc.isEmpty()) {
            ra.addFlashAttribute("error", "의미를 입력하세요.");
            ra.addFlashAttribute("dto", vocaDTO);
            return "redirect:/study/vocaInsert";
        }

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
    public String vocaUpdateProc(VocaDTO vocaDTO, RedirectAttributes ra) {
        String vocab = vocaDTO.getVocabulary();
        String desc  = vocaDTO.getDescription();

        // 단어: 공백/빈 값 금지
        if (vocab == null || vocab.trim().isEmpty()) {
            ra.addFlashAttribute("error", "단어를 입력하세요.");
            return "redirect:/study/vocaUpdate/" + vocaDTO.getVocaId();
        }
        // 의미: 완전 빈 값 금지(공백은 허용)
        if (desc == null || desc.isEmpty()) {
            ra.addFlashAttribute("error", "의미를 입력하세요.");
            return "redirect:/study/vocaUpdate/" + vocaDTO.getVocaId();
        }

        vocaService.setUpdate(vocaDTO);
        return "redirect:/study/vocaList/admin";
    }

    @GetMapping("/vocaDelete/{id}")
    public String vocaDelete(@PathVariable int id) {
        VocaDTO searchDTO = new VocaDTO();
        searchDTO.setVocaId(id);
        vocaService.setDelete(searchDTO);
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
