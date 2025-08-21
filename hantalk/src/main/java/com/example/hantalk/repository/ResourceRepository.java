package com.example.hantalk.repository;

import com.example.hantalk.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Integer> {

    // 저장된 파일 경로의 끝부분(파일명)으로 Resource 검색
    Optional<Resource> findByArchiveEndingWith(String storedFileName);

    // 제목 또는 내용에 검색어가 포함된 자료 검색
    Page<Resource> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String titleKeyword, String contentKeyword, Pageable pageable);

    // 💡💡💡 추가해야 할 메서드 💡💡💡
    Optional<Resource> findByArchive(String fullPath);
}

