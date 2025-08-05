package com.example.hantalk.controller;

import com.example.hantalk.dto.CommentDTO;
import com.example.hantalk.entity.Users;
import com.example.hantalk.service.CommentService;
import jakarta.servlet.http.HttpSession;
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

    //댓글 작성
    @PostMapping("/chuga")
    public String chuga(@ModelAttribute CommentDTO commentDTO, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "redirect:/user/login";
        }
        Users user = new Users();
        user.setUserNo(userNo);
        commentDTO.setUsers(user);

        commentService.save(commentDTO);
        return "redirect:/post/view/" + commentDTO.getPost().getPostId();
    }

    // 댓글 수정
    @GetMapping("/sujung")
    public String sujung(@RequestParam("id") Long commentId, Model model) {
        CommentDTO commentDTO = commentService.getCommentById(commentId);
        model.addAttribute("commentDTO", commentDTO);
        return "comment/sujung";
    }

    // 댓글 수정 처리
    @PostMapping("/sujungProc")
    public String sujungProc(@ModelAttribute CommentDTO commentDTO) {
        commentService.setUpdate(commentDTO);
        return "redirect:/post/view/" + commentDTO.getPost().getPostId();
    }

    // 댓글 삭제
    @GetMapping("/sakje")
    public String sakje(@RequestParam("id") Long commentId, @RequestParam("postId") int postId) {
        commentService.setDelete(commentId);
        return "redirect:/post/view/" + postId;
    }
}