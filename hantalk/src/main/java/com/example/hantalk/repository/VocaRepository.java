package com.example.hantalk.repository;

import com.example.hantalk.entity.Voca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VocaRepository extends JpaRepository<Voca, Integer> {

    Optional<Voca> findByVocabulary (String Vocabulary);

    @Query
    (value = "SElECT * FROM voca ORDER BY RAND() LIMIT :n ", nativeQuery = true) // DB에서 랜덤으로 n개의 단어를 가져오는 메서드
    List<Voca> findByRandomVocas(@Param("n") int n);


}
