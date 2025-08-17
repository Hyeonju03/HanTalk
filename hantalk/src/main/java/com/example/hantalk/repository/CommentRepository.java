package com.example.hantalk.repository;

import com.example.hantalk.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 게시물 ID(postId)로 댓글 목록을 조회하는 메서드
    List<Comment> findByPost_PostId(int postId);

}


