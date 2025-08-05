package com.example.hantalk.repository;

import com.example.hantalk.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SentenceRepository extends JpaRepository<Sentence, Integer> {
    @Query(value = "SELECT * FROM sentence order by RAND() limit 1", nativeQuery = true)
    Optional<Sentence> findByRandom();
}
