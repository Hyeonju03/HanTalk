package com.example.hantalk.repository;

import com.example.hantalk.entity.LogData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogDataRepository extends JpaRepository<LogData,Integer> {
    Optional<List<LogData>> findByCreateDateBetween(LocalDateTime start, LocalDateTime end);

    Optional<List<LogData>> findByUserNo(int userNo);

    Optional<List<LogData>> findByUserNoAndCreateDateBetween(int userNo, LocalDateTime start, LocalDateTime end);
}
