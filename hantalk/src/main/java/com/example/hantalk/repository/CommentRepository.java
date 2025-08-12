package com.example.hantalk.repository;

import com.example.hantalk.entity.Comment;
import com.example.hantalk.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost_PostId(int postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.post = :post")
    void deleteByPost(@Param("post") Post post);
}