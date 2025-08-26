package com.example.hantalk.repository;

import com.example.hantalk.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    // ✅ 파일명으로 영상 존재 여부 확인
    boolean existsByVideoName(String videoName);

    // 🔍 제목으로 영상 검색 (페이징 포함)
    Page<Video> findByTitleContaining(String title, Pageable pageable);

    // 🔍 내용으로 영상 검색 (페이징 포함)
    Page<Video> findByContentContaining(String content, Pageable pageable);

    // 🔍 파일명으로 영상 검색 (페이징 포함)
    Page<Video> findByVideoNameContaining(String videoName, Pageable pageable);

    // 🔍 제목, 내용, 파일명으로 영상 검색 (페이징 포함)
    @Query("SELECT v FROM Video v WHERE " +
            "v.title LIKE %:keyword% OR v.content LIKE %:keyword% OR v.videoName LIKE %:keyword%")
    Page<Video> findByTitleOrContentOrVideoNameContaining(@Param("keyword") String keyword, Pageable pageable);

    // 특정 비디오의 조회수를 1 증가시키는 쿼리 (update문)
    // @Modifying 어노테이션은 데이터 변경 쿼리임을 명시합니다.
    @Modifying
    @Query("UPDATE Video v SET v.viewHit = v.viewHit + 1 WHERE v.videoId = :videoId")
    void incrementViewHit(@Param("videoId") int videoId);

    // 🔍 제목으로 영상 검색 (List 반환, 이전 코드에서 사용)
    List<Video> findByTitleContaining(String title);

    // 🔍 내용으로 영상 검색 (List 반환, 이전 코드에서 사용)
    List<Video> findByContentContaining(String content);

    // 🔍 제목 또는 내용으로 영상 검색 (List 반환, 이전 코드에서 사용)
    List<Video> findByTitleContainingOrContentContaining(String title, String content);

}
