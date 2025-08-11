package com.example.hantalk.repository;

import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
;import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Page<Post> findByCategory_CategoryId(Integer categoryId, Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String keyword, String keyword1, Pageable pageable);

    @Query("SELECT p.archive FROM Post p WHERE p.archive LIKE CONCAT(:fileId, '%')")
    String findArchiveByFileId(@Param("fileId") String fileId);

    @Query(value = "SELECT original_name FROM post WHERE archive LIKE CONCAT(:fileId, '%')", nativeQuery = true)
    String findOriginalNameByFileId(@Param("fileId") String fileId);

    @Query("select p from Post p where p.category.id in :categoryIds order by p.createDate desc")
    List<Post> findByCategoryIdIn(@Param("categoryIds") List<Integer> categoryIds); //어드민이 한번에 모아보는 것 관련

    List<Post> findByCategory_CategoryId(Integer categoryId); //어드민이 보는 페이지에서 카테고리 나누는 거
}


