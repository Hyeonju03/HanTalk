package com.example.hantalk.repository;

import com.example.hantalk.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    List<Video> findByTitleContaining(String keyword);
    List<Video> findByContentContaining(String keyword);
    List<Video> findByTitleContainingOrContentContaining(String keyword1, String keyword2);
}


