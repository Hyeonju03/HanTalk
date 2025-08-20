package com.example.hantalk.service;

import com.example.hantalk.dto.CommentDTO;
import com.example.hantalk.entity.Comment;
import com.example.hantalk.entity.Post;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.CommentRepository;
import com.example.hantalk.repository.PostRepository;
import com.example.hantalk.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public CommentDTO createComment(CommentDTO dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));
        Users user = usersRepository.findById(dto.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        Comment comment = toEntity(dto, post, user);

        Comment savedComment = commentRepository.save(comment);
        return toDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(int postId) {
        List<Comment> comments = commentRepository.findByPost_PostId(postId);
        return comments.stream().map(this::toDto).collect(Collectors.toList());
    }

    // 댓글 수정 (DTO를 매개변수로 받도록 변경)
    @Transactional
    public CommentDTO updateComment(int commentId, CommentDTO dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // DTO에서 userNo를 가져와 권한 확인
        if (!comment.getUsers().getUserNo().equals(dto.getUserNo())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        comment.setContent(dto.getContent());

        return toDto(comment);
    }

    // 댓글 삭제 (DTO를 매개변수로 받도록 변경)
    @Transactional
    public void deleteComment(int commentId, CommentDTO dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // DTO에서 userNo를 가져와 권한 확인
        if (!comment.getUsers().getUserNo().equals(dto.getUserNo())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    private Comment toEntity(CommentDTO dto, Post post, Users user) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setPost(post);
        comment.setUsers(user);
        return comment;
    }

    private CommentDTO toDto(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setContent(comment.getContent());
        dto.setCreateDate(comment.getCreateDate());
        dto.setUpdateDate(comment.getUpdateDate());
        dto.setPostId(comment.getPost().getPostId());
        dto.setUserNo(comment.getUsers().getUserNo());
        dto.setNickName(comment.getUsers().getNickname());
        return dto;
    }
}