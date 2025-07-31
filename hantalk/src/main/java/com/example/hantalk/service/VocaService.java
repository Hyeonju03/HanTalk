package com.example.hantalk.service;

import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.entity.Voca;
import com.example.hantalk.repository.VocaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VocaService {

    private final VocaRepository vocaRepository;

    // 학습 1번 (단어 맞추기)
    public List<VocaDTO> generateFillBlank(int count) {
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

}
