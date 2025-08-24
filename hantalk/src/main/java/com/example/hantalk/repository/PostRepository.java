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
    Page<Post> findByCategory_CategoryId(Integer categoryId, Pageable pageable);

    // 제목 검색
    Page<Post> findByCategory_CategoryIdAndTitleContaining(Integer categoryId, String keyword, Pageable pageable);

    // 내용 검색
    Page<Post> findByCategory_CategoryIdAndContentContaining(Integer categoryId, String keyword, Pageable pageable);

    // 작성자 이름 검색 (nickname으로 변경)
    Page<Post> findByCategory_CategoryIdAndUsers_NicknameContaining(Integer categoryId, String keyword, Pageable pageable);

    // 일반 게시판에서 제목 또는 내용으로 검색
    @Query("SELECT p FROM Post p WHERE p.category.categoryId = :categoryId AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchByTitleOrContent(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword, Pageable pageable);

    // --- 문의사항 게시판 전용 메서드 추가 ---

    // 특정 카테고리에서 특정 사용자의 모든 게시물
    Page<Post> findByCategory_CategoryIdAndUsers_UserNo(Integer categoryId, Integer userNo, Pageable pageable);

    // 특정 카테고리에서 특정 사용자의 제목 검색
    Page<Post> findByCategory_CategoryIdAndUsers_UserNoAndTitleContaining(Integer categoryId, Integer userNo, String keyword, Pageable pageable);

    // 특정 카테고리에서 특정 사용자의 내용 검색
    Page<Post> findByCategory_CategoryIdAndUsers_UserNoAndContentContaining(Integer categoryId, Integer userNo, String keyword, Pageable pageable);

    // 문의사항 게시판에서 로그인한 사용자의 글 중 제목 또는 내용으로 검색
    @Query("SELECT p FROM Post p WHERE p.category.categoryId = :categoryId AND p.users.userNo = :userNo AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchByTitleOrContentAndUser(@Param("categoryId") Integer categoryId, @Param("userNo") Integer userNo, @Param("keyword") String keyword, Pageable pageable);

    // 문의사항 게시판에서 로그인한 사용자의 글 중 작성자(nickname)로 검색 (JPQL 쿼리 변경)
    @Query("SELECT p FROM Post p WHERE p.category.categoryId = :categoryId AND p.users.userNo = :userNo AND p.users.nickname LIKE %:keyword%")
    Page<Post> searchByUserAndAuthor(@Param("categoryId") Integer categoryId, @Param("userNo") Integer userNo, @Param("keyword") String keyword, Pageable pageable);

    // =========================================================================
    // ✅ PostService의 adminListPost 메서드를 위한 전체 게시물 검색 기능 추가
    // =========================================================================

    // 전체 게시물에서 제목 검색
    Page<Post> findByTitleContaining(String keyword, Pageable pageable);

    // 전체 게시물에서 내용 검색
    Page<Post> findByContentContaining(String keyword, Pageable pageable);

    // 전체 게시물에서 작성자 이름 검색
    Page<Post> findByUsers_NicknameContaining(String keyword, Pageable pageable);

    // 전체 게시물에서 제목 또는 내용으로 검색
    Page<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);

    // 파일 이름으로 게시물 찾기
    Optional<Post> findByArchive(String archive);
}
