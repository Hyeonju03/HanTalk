package com.example.hantalk.repository;

import com.example.hantalk.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Page<Post> findByCategory_CategoryId(Integer categoryId, Pageable pageable);

}

