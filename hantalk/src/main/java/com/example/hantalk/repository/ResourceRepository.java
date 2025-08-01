package com.example.hantalk.repository;

import com.example.hantalk.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
    // 기본 CRUD 제공
}