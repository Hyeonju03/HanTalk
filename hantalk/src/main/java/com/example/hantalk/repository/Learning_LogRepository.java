package com.example.hantalk.repository;

import com.example.hantalk.entity.Learning_Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface Learning_LogRepository extends JpaRepository<Learning_Log,Integer> {
    boolean existsByUsers_UserNoAndLearningDateBetween(int userNo, LocalDateTime startOfDay, LocalDateTime endOfDay);

    Optional<Learning_Log>
    findByUsers_UserNoAndLearningDateBetween(int userNo, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
