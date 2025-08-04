package com.example.hantalk.repository;

import com.example.hantalk.entity.Leaning_Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface Leaning_LogRepository extends JpaRepository<Leaning_Log,Integer> {
    boolean existsByUsers_UserNoAndLearningDateBetween(int userNo, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
