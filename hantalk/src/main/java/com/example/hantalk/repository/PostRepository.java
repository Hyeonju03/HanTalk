package com.example.hantalk.repository;

import com.example.hantalk.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    // 특정 카테고리의 모든 게시물
    Page<Post> findByCategory_CategoryId(int categoryId, Pageable pageable);

    // 제목 검색
    Page<Post> findByCategory_CategoryIdAndTitleContaining(int categoryId, String keyword, Pageable pageable);

    // 내용 검색
    Page<Post> findByCategory_CategoryIdAndContentContaining(int categoryId, String keyword, Pageable pageable);

    // 작성자 이름 검색 (nickname으로 변경)
    Page<Post> findByCategory_CategoryIdAndUsers_NicknameContaining(int categoryId, String keyword, Pageable pageable);

    // 일반 게시판에서 제목 또는 내용으로 검색
    @Query("SELECT p FROM Post p WHERE p.category.categoryId = :categoryId AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchByTitleOrContent(@Param("categoryId") int categoryId, @Param("keyword") String keyword, Pageable pageable);

    // --- 문의사항 게시판 전용 메서드 추가 ---

    // 특정 카테고리에서 특정 사용자의 모든 게시물
    Page<Post> findByCategory_CategoryIdAndUsers_UserNo(int categoryId, int userNo, Pageable pageable);

    // 특정 카테고리에서 특정 사용자의 제목 검색
    Page<Post> findByCategory_CategoryIdAndUsers_UserNoAndTitleContaining(int categoryId, int userNo, String keyword, Pageable pageable);

    // 특정 카테고리에서 특정 사용자의 내용 검색
    Page<Post> findByCategory_CategoryIdAndUsers_UserNoAndContentContaining(int categoryId, int userNo, String keyword, Pageable pageable);

    // 문의사항 게시판에서 로그인한 사용자의 글 중 제목 또는 내용으로 검색
    @Query("SELECT p FROM Post p WHERE p.category.categoryId = :categoryId AND p.users.userNo = :userNo AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchByTitleOrContentAndUser(@Param("categoryId") int categoryId, @Param("userNo") int userNo, @Param("keyword") String keyword, Pageable pageable);

    // 문의사항 게시판에서 로그인한 사용자의 글 중 작성자(nickname)로 검색 (JPQL 쿼리 변경)
    @Query("SELECT p FROM Post p WHERE p.category.categoryId = :categoryId AND p.users.userNo = :userNo AND p.users.nickname LIKE %:keyword%")
    Page<Post> searchByUserAndAuthor(@Param("categoryId") int categoryId, @Param("userNo") int userNo, @Param("keyword") String keyword, Pageable pageable);
}