package com.example.hantalk.repository;

import com.example.hantalk.entity.Learning_Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface Learning_LogRepository extends JpaRepository<Learning_Log,Integer> {
    boolean existsByUsers_UserNoAndLearningDateBetween(int userNo, LocalDateTime startOfDay, LocalDateTime endOfDay);

    Optional<Learning_Log>
    findByUsers_UserNoAndLearningDateBetween(int userNo, LocalDateTime startOfDay, LocalDateTime endOfDay);

    // 특정 사용자의 모든 학습 기록 엔티티를 조회하는 메서드
    List<Learning_Log> findByUsers_UserNoOrderByLearningDateAsc(int userNo);

    // 특정 사용자의 특정 기간 내의 학습 기록 엔티티를 조회하는 메서드
    List<Learning_Log> findByUsers_UserNoAndLearningDateBetweenOrderByLearningDateAsc(int userNo, LocalDateTime startDate, LocalDateTime endDate);
}
