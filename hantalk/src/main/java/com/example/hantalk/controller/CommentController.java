package com.example.hantalk.controller;

import com.example.hantalk.dto.CommentDTO;
import com.example.hantalk.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 리스트 보기
    @GetMapping("/list")
    public String list(Model model) {
        List<CommentDTO> commentList = commentService.getAllComments();
        model.addAttribute("commentList", commentList);
        return "comment/list";
    }

    // 댓글 수정 화면 이동
    @GetMapping("/sujung")
    public String sujung(@RequestParam("id") Long commentId, Model model) {
        CommentDTO commentDTO = commentService.getCommentById(commentId);
        model.addAttribute("commentDTO", commentDTO); // 단수 형태로 명확하게
        return "comment/sujung";
    }

    // 댓글 수정 처리
    @PostMapping("/sujungProc")
    public String sujungProc(@ModelAttribute CommentDTO commentDTO) {
        commentService.setUpdate(commentDTO);
        return "redirect:/comments/list";
    }

    // 댓글 삭제
    @GetMapping("/sakje")
    public String sakje(@RequestParam("id") Long commentId) {
        commentService.setDelete(commentId);
        return "redirect:/comments/list";
    }
}
