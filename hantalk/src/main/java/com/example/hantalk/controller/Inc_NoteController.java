package com.example.hantalk.controller;

import com.example.hantalk.entity.Inc_Note;
import com.example.hantalk.service.Inc_NoteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class Inc_NoteController {

    private final Inc_NoteService incNoteService;

    // 오답 체크 후 저장 / 삭제 처리
    @PostMapping("/check")
    @ResponseBody
    public boolean checkAnswer(@RequestParam String type, @RequestParam int targetId,
                               @RequestParam String answer, @RequestParam String correctAnswer,
                               HttpSession session) {

        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        boolean isCorrect = answer.equals(correctAnswer);

        if (isCorrect) {
            // 정답 → 기존 오답노트에서 제거
            if (type.equals("word")) {
                incNoteService.deleteIncorrectNote(userNo, targetId, null);
            } else {
                incNoteService.deleteIncorrectNote(userNo, null, targetId);
            }
        } else {
            // 오답 → 오답노트에 추가
            if (type.equals("word")) {
                incNoteService.saveIncorrectNote(userNo, targetId, null);
            } else {
                incNoteService.saveIncorrectNote(userNo, null, targetId);
            }
        }
        return isCorrect;
    }

    // 오답노트 페이지
    @GetMapping("/note")
    public String getNotes(Model model, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");

        List<Inc_Note> notes = incNoteService.findNotes(userNo);

        List<Inc_Note> wordNotes = notes.stream()
                .filter(n -> n.getVoca() != null)
                .toList();

        List<Inc_Note> sentenceNotes = notes.stream()
                .filter(n -> n.getSentence() != null)
                .toList();

        model.addAttribute("notes", notes);
        model.addAttribute("wordNotes", wordNotes); // 단어만
        model.addAttribute("sentenceNotes", sentenceNotes); // 문장만

        return "study/note";
    }
}
