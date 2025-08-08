package com.example.hantalk.repository;

import com.example.hantalk.entity.Voca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocaRepository extends JpaRepository<Voca, Integer> {

    @Query
    (value = "SELECT * FROM voca v WHERE v.voca_id NOT IN :excludeIds ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Voca> findRandomVocas(@Param("excludeIds") List<Integer> excludeIds,
                               @Param("excludeSize") int excludeSize,
                               @Param("count") int count);  // 랜덤 추출 (중복 방지)
}
