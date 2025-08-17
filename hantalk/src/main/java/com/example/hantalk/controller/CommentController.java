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
        // 특정 게시물에 대한 댓글 목록을 조회합니다.
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 생성 (POST /api/comments/create)
    @PostMapping("/create")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO dto, HttpSession session) {
        // 세션에서 로그인한 사용자의 userNo를 가져옵니다.
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);

        // 로그인하지 않은 사용자라면 UNAUTHORIZED 상태 코드를 반환합니다.
        if (loginUserNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // DTO에 로그인한 사용자의 userNo를 설정합니다.
        dto.setUserNo(loginUserNo);

        try {
            // 댓글 생성 서비스 메서드를 호출합니다.
            CommentDTO createdComment = commentService.createComment(dto);
            // 성공 시 CREATED 상태 코드와 함께 생성된 댓글 정보를 반환합니다.
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (IllegalArgumentException e) {
            // 게시물 또는 사용자가 존재하지 않을 경우 BAD_REQUEST를 반환합니다.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 댓글 삭제 (DELETE /api/comments/{commentId})
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int commentId, HttpSession session) {
        // 세션에서 로그인한 사용자의 userNo를 가져옵니다.
        Integer loginUserNo = SessionUtil.getLoginUserNo(session);

        // 로그인하지 않은 사용자라면 UNAUTHORIZED 상태 코드를 반환합니다.
        if (loginUserNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // TODO: 댓글 작성자 또는 관리자만 삭제할 수 있도록 권한 검증 로직을 추가해야 합니다.
        // 현재는 모든 로그인 사용자가 삭제 가능합니다.

        try {
            commentService.deleteComment(commentId);
            // 성공적으로 삭제되면 콘텐츠 없음(NO_CONTENT) 상태 코드를 반환합니다.
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            // 댓글이 존재하지 않을 경우 NOT_FOUND 상태 코드를 반환합니다.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
