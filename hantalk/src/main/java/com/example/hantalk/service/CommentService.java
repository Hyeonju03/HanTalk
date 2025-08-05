package com.example.hantalk.service;

import com.example.hantalk.dto.CommentDTO;
import com.example.hantalk.entity.Comment;
import com.example.hantalk.entity.Post;
import com.example.hantalk.repository.CommentRepository;
import com.example.hantalk.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 전체
    public List<CommentDTO> getAllComments() {
        List<Comment> commentList = commentRepository.findAll();
        return commentList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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

    public void save(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());

        Post post = postRepository.findById(commentDTO.getPost().getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + commentDTO.getPost().getPostId()));

        if (commentDTO.getUsers() != null) {
            comment.setUsers(commentDTO.getUsers());
        } else {
            throw new IllegalArgumentException("댓글 작성자 정보가 없습니다.");
        }

        comment.setPost(post);
        commentRepository.save(comment);
    }

    public CommentDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(Math.toIntExact(commentId)) //문제 있으면 삭제해보시오
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + commentId));
        return convertToDto(comment);
    }

    public List<CommentDTO> getCommentsByPostId(int postId) {
        List<Comment> comments = commentRepository.findByPost_PostId(postId);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

}
