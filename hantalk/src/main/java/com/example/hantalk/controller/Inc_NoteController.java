package com.example.hantalk.controller;

import com.example.hantalk.service.Inc_NoteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lesson")
public class Inc_NoteController {

    private final Inc_NoteService incNoteService;

    @PostMapping("/check")
    public boolean checkAnswer(@RequestParam String type, @RequestParam int targetId, @RequestParam String answer,
                               @RequestParam String correctAnswer, HttpSession session) {

        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        boolean isCorrect = answer.equals(correctAnswer);

        if (isCorrect) {
            if (type.equals("voca")) {
                incNoteService.deleteIncorrectNote(userNo, targetId, null);
            } else {
                incNoteService.deleteIncorrectNote(userNo, null, targetId);
            }
        } else {
            if (type.equals("voca")) {
                incNoteService.saveIncorrectNote(userNo, targetId, null);
            } else {
                incNoteService.saveIncorrectNote(userNo, null, targetId);
            }
        }
        return isCorrect;
    }
}
