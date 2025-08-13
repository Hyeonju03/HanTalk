package com.example.hantalk.repository;

import com.example.hantalk.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    // 특정 카테고리의 모든 게시물을 페이지네이션하여 조회
    Page<Post> findByCategory_CategoryId(int categoryId, Pageable pageable);

    // 특정 카테고리에서 제목으로 검색 (페이지네이션 적용)
    Page<Post> findByCategory_CategoryIdAndTitleContaining(int categoryId, String keyword, Pageable pageable);

    // 특정 카테고리에서 내용으로 검색 (페이지네이션 적용)
    Page<Post> findByCategory_CategoryIdAndContentContaining(int categoryId, String keyword, Pageable pageable);

    // 특정 카테고리에서 작성자 이름으로 검색 (페이지네이션 적용)
    Page<Post> findByCategory_CategoryIdAndUsers_UsernameContaining(int categoryId, String keyword, Pageable pageable);

    // 특정 카테고리에서 제목 또는 내용으로 검색 (페이지네이션 적용)
    Page<Post> findByCategory_CategoryIdAndTitleContainingOrCategory_CategoryIdAndContentContaining(int categoryId, String titleKeyword, int categoryId2, String contentKeyword, Pageable pageable);
}