//package com.example.hantalk.controller;
//
//import com.example.hantalk.dto.CommentDTO;
//import com.example.hantalk.dto.PostDTO;
//import com.example.hantalk.entity.Users;
//import com.example.hantalk.service.CommentService;
//import com.example.hantalk.service.PostService;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/comments")
//public class CommentController {
//
//    private final CommentService commentService;
//    private final PostService postService;
//
//    //댓글 작성
//    @PostMapping("/chuga")
//    public String chuga(@ModelAttribute CommentDTO commentDTO, HttpSession session) {
//        Integer userNo = (Integer) session.getAttribute("userNo");
//        Object roleObj = session.getAttribute("role");
//        String role = (roleObj != null) ? roleObj.toString() : "";
//
//        if (userNo == null && !"ADMIN".equalsIgnoreCase(role)) {
//            return "redirect:/user/login";
//        }
//
//        PostDTO postDTO = postService.getSelectOneById(Math.toIntExact(commentDTO.getPostId()));
//
//        if (postDTO.getCategoryId() == 3 && !"ADMIN".equalsIgnoreCase(role)) {
//            return "redirect:/post/view/" + commentDTO.getPostId() + "?error=not_authorized";
//        }
//
//        Users user = new Users();
//        if(userNo != null) {
//            user.setUserNo(userNo);
//        } else {
//
//            user.setUserNo(1);
//        }
//        commentDTO.setUsers(user);
//
//        commentService.save(commentDTO);
//        return "redirect:/post/view/" + commentDTO.getPostId();
//    }
//
//
//    // 댓글 수정
//    @GetMapping("/sujung")
//    public String sujung(@RequestParam("id") Long commentId, Model model) {
//        CommentDTO commentDTO = commentService.getCommentById(commentId);
//        model.addAttribute("commentDTO", commentDTO);
//        return "comment/sujung";
//    }
//
//    // 댓글 수정 처리
//    @PostMapping("/sujungProc")
//    public String sujungProc(@ModelAttribute CommentDTO commentDTO) {
//        commentService.setUpdate(commentDTO);
//        return "redirect:/post/view/" + commentDTO.getPostId();
//    }
//
//    // 댓글 삭제
//    @GetMapping("/sakje")
//    public String sakje(@RequestParam("id") Long commentId, @RequestParam("postId") int postId) {
//        commentService.setDelete(commentId);
//        return "redirect:/post/view/" + postId;
//    }
//}