package com.example.hantalk.service;


import com.example.hantalk.entity.Inc_Note;
import com.example.hantalk.entity.Sentence;
import com.example.hantalk.entity.Users;
import com.example.hantalk.entity.Voca;
import com.example.hantalk.repository.Inc_NoteRepository;
import com.example.hantalk.repository.SentenceRepository;
import com.example.hantalk.repository.UsersRepository;
import com.example.hantalk.repository.VocaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Inc_NoteService {

    private final UsersRepository usersRepository;
    private final VocaRepository vocaRepository;
    private final SentenceRepository sentenceRepository;
    private final Inc_NoteRepository incNoteRepository;

    @Transactional
    public void saveIncorrectNote(int userNo, Integer vocaId, Integer sentenceId) {
        Users user = usersRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Inc_Note note = new Inc_Note();
        note.setUsers(user);

        if (vocaId != null) {
            Voca voca = vocaRepository.findById(vocaId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단어입니다."));
            note.setVoca(voca);
        }

        if (sentenceId != null) {
            Sentence sentence = sentenceRepository.findById(sentenceId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문장입니다."));
            note.setSentence(sentence);
        }

        incNoteRepository.save(note);
    }

    @Transactional
    public void deleteIncorrectNote(int userNo, Integer vocaId, Integer sentenceId) {
        if (vocaId != null) {
            incNoteRepository.deleteVocaNote(userNo, vocaId);
        }
        if (sentenceId != null) {
            incNoteRepository.deleteSentenceNote(userNo, sentenceId);
        }
    }
}
