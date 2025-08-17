package com.example.hantalk.repository;

import com.example.hantalk.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    // 특정 카테고리의 모든 게시물
    Page<Post> findByCategory_CategoryId(int categoryId, Pageable pageable);

    // 제목 검색
    Page<Post> findByCategory_CategoryIdAndTitleContaining(int categoryId, String keyword, Pageable pageable);

    // 내용 검색
    Page<Post> findByCategory_CategoryIdAndContentContaining(int categoryId, String keyword, Pageable pageable);

    // 작성자 이름 검색
    Page<Post> findByCategory_CategoryIdAndUsers_UsernameContaining(int categoryId, String keyword, Pageable pageable);

    // 제목 또는 내용 검색 (카테고리 ID 동일)
    @Query("SELECT p FROM Post p WHERE p.category.categoryId = :categoryId AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchByTitleOrContent(@Param("categoryId") int categoryId,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);

    // UUID로 저장된 파일명을 기준으로 게시물을 찾는 메서드 추가
    Optional<Post> findByArchive(String archive);
}