package com.example.hantalk.repository;

import com.example.hantalk.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost_PostId(int postId);
}
