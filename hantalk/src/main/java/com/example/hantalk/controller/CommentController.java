package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.dto.CommentDTO;
import com.example.hantalk.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 목록 조회 (GET /api/comments/post/{postId})
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable int postId) {
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 생성 (POST /api/comments/create)
    @PostMapping("/create")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO dto, HttpSession session) {
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);

        if (loginUserNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        dto.setUserNo(loginUserNo);

        try {
            CommentDTO createdComment = commentService.createComment(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 댓글 수정 (PUT /api/comments/{commentId})
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable int commentId,
            @RequestBody CommentDTO updateDTO,
            HttpSession session) {

        Integer loginUserNo = SessionUtil.getLoginUserNo(session);
        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");

        if (loginUserNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        updateDTO.setUserNo(loginUserNo);

        try {
            // ADMIN 권한을 가진 사용자는 모든 댓글 수정 가능
            if (isAdmin) {
                CommentDTO updatedComment = commentService.updateCommentForAdmin(commentId, updateDTO);
                return ResponseEntity.ok(updatedComment);
            } else {
                // 일반 사용자는 자신의 댓글만 수정 가능
                CommentDTO updatedComment = commentService.updateComment(commentId, updateDTO);
                return ResponseEntity.ok(updatedComment);
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 댓글 삭제 (DELETE /api/comments/{commentId})
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int commentId, HttpSession session) {
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);
        boolean isAdmin = SessionUtil.hasRole(session, "ADMIN");

        if (loginUserNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CommentDTO dto = new CommentDTO();
        dto.setCommentId(commentId);
        dto.setUserNo(loginUserNo);

        try {
            // ADMIN 권한을 가진 사용자는 모든 댓글 삭제 가능
            if (isAdmin) {
                commentService.deleteCommentForAdmin(commentId);
                return ResponseEntity.noContent().build();
            } else {
                // 일반 사용자는 자신의 댓글만 삭제 가능
                commentService.deleteComment(commentId, dto);
                return ResponseEntity.noContent().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}