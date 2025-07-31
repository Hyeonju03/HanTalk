package com.example.hantalk.service;

import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.entity.Voca;
import com.example.hantalk.repository.VocaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VocaService {

    private final VocaRepository vocaRepository;

    // 학습 1번 (단어 맞추기)
    public List<VocaDTO> getFillBlank(int count) {
        List<Voca> vocas = vocaRepository.findByRandomVocas(count);

        List<VocaDTO> result = new ArrayList<>();

        for (Voca v : vocas) {
            VocaDTO dto = new VocaDTO();
            dto.setVocaId(v.getVocaId());
            dto.setVocabulary(v.getVocabulary());
            dto.setDescription(v.getDescription());
            dto.setCreateDate(v.getCreateDate());
            dto.setIncNoteList(v.getIncNoteList());

            result.add(dto);
        }
        return result;
    }

    // 학습 3번 (4지선다 객관식)
    public List<Map<String, Object>> getMultipleChoice(int count) {
        List<Map<String, Object>> questions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            List<Voca> options = vocaRepository.findByRandomVocas(4);

            Voca correct = options.get(0);
            String description = correct.getDescription();

            List<String> choices = new ArrayList<>();
            for (Voca v : options) {
                choices.add(v.getVocabulary());
            }
            Collections.shuffle(choices);

            Map<String, Object> question = new HashMap<>();
            question.put("description", description);
            question.put("options", choices);

            questions.add(question);
        }

        return questions;
    }
}
