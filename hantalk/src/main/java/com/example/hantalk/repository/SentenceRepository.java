package com.example.hantalk.repository;

import com.example.hantalk.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentenceRepository extends JpaRepository<Sentence, Integer> {
}
