package com.example.hantalk.service;

import com.example.hantalk.dto.CommentDTO;
import com.example.hantalk.entity.Comment;
import com.example.hantalk.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // 댓글 전체
    public List<CommentDTO> getAllComments() {
        List<Comment> commentList = commentRepository.findAll();
        return commentList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 댓글 조회
    public CommentDTO getCommentById(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(Math.toIntExact(commentId));
        return optionalComment.map(this::convertToDto).orElse(null);
    }

    // 댓글 수정 처리
    public void setUpdate(CommentDTO commentDTO) {
        Optional<Comment> optionalComment = commentRepository.findById(commentDTO.getCommentId());
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setContent(commentDTO.getContent());
            commentRepository.save(comment);
        }
    }

    // 댓글 삭제 처리
    public void setDelete(Long commentId) {
        commentRepository.deleteById(Math.toIntExact(commentId));
    }

    // Entity > DTO 변환
    private CommentDTO convertToDto(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setContent(comment.getContent());
        dto.setPostId(comment.getPost().getPostId());
        return dto;
    }
}
