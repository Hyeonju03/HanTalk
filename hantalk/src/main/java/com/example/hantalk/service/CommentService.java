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

    /**
     * 댓글을 등록합니다.
     * @param dto 댓글 정보가 담긴 DTO
     * @return 생성된 댓글 정보가 담긴 DTO
     */
    @Transactional
    public CommentDTO createComment(CommentDTO dto) {
        // 게시물 ID로 게시글 엔티티를 찾습니다.
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        // 사용자 ID로 사용자 엔티티를 찾습니다.
        Users user = usersRepository.findById(dto.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // DTO를 엔티티로 변환합니다.
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setPost(post);
        comment.setUsers(user);

        // 댓글을 저장하고 저장된 엔티티를 다시 DTO로 변환하여 반환합니다.
        Comment savedComment = commentRepository.save(comment);
        return toDto(savedComment);
    }

    /**
     * 특정 게시물의 댓글 목록을 조회합니다.
     * @param postId 게시물 ID
     * @return 해당 게시물의 댓글 목록 DTO
     */
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(int postId) {
        // 게시물 ID로 모든 댓글 엔티티를 찾습니다.
        List<Comment> comments = commentRepository.findByPost_PostId(postId);
        // 엔티티 목록을 DTO 목록으로 변환하여 반환합니다.
        return comments.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * 댓글을 삭제합니다.
     * @param commentId 삭제할 댓글 ID
     */
    @Transactional
    public void deleteComment(int commentId) {
        commentRepository.deleteById(commentId);
    }

    /**
     * 댓글을 수정합니다.
     * @param commentId 수정할 댓글 ID
     * @param dto 수정 정보가 담긴 DTO
     * @return 수정된 댓글 정보가 담긴 DTO
     */
    @Transactional
    public CommentDTO updateComment(int commentId, CommentDTO dto) {
        // 댓글 ID로 기존 댓글을 찾습니다.
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 댓글 내용을 업데이트합니다.
        comment.setContent(dto.getContent());

        // JPA의 변경 감지(Dirty Checking) 기능으로 인해 save()를 호출할 필요가 없습니다.
        return toDto(comment);
    }


    /**
     * 엔티티를 DTO로 변환합니다.
     * @param comment 변환할 Comment 엔티티
     * @return 변환된 CommentDTO
     */
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
