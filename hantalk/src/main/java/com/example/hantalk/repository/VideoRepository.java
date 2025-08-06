package com.example.hantalk.repository;

import com.example.hantalk.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {

    // 일반 검색 (페이징 없이 사용)
    List<Video> findByTitleContaining(String keyword);
    List<Video> findByContentContaining(String keyword);
    List<Video> findByTitleContainingOrContentContaining(String keyword1, String keyword2);

    // 페이징 검색 (페이징 처리를 위한 메서드들)
    Page<Video> findByTitleContaining(String keyword, Pageable pageable);
    Page<Video> findByContentContaining(String keyword, Pageable pageable);
    Page<Video> findByTitleContainingOrContentContaining(String keyword1, String keyword2, Pageable pageable);
}
