package com.example.hantalk.repository;

import com.example.hantalk.entity.Inc_Note;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Inc_NoteRepository extends JpaRepository<Inc_Note, Integer> {

    @EntityGraph(attributePaths = {"voca", "sentence"})
    List<Inc_Note> findByUsers_UserNo(int userNo);

    // 중복 저장 방지용 (옵션)
    boolean existsByUsers_UserNoAndVoca_VocaId(int userNo, int vocaId);
    boolean existsByUsers_UserNoAndSentence_SentenceId(int userNo, int sentenceId);

    @Modifying
    @Query("DELETE FROM Inc_Note n WHERE n.users.userNo = :userNo AND n.voca.vocaId = :vocaId")
    void deleteVocaNote(@Param("userNo") int userNo, @Param("vocaId") int vocaId);

    @Modifying
    @Query("DELETE FROM Inc_Note n WHERE n.users.userNo = :userNo AND n.sentence.sentenceId = :sentenceId")
    void deleteSentenceNote(@Param("userNo") int userNo, @Param("sentenceId") int sentenceId);

}
