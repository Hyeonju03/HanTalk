package com.example.hantalk.service;

import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.entity.Voca;
import com.example.hantalk.repository.VocaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VocaService {

    private final VocaRepository vocaRepository;

    // entity → dto
    private VocaDTO entityToDto(Voca voca) {
        VocaDTO dto = new VocaDTO();
        dto.setVocaId(voca.getVocaId());
        dto.setVocabulary(voca.getVocabulary());
        dto.setDescription(voca.getDescription());
        dto.setCreateDate(voca.getCreateDate());
        dto.setIncNoteList(voca.getIncNoteList());
        return dto;
    }

    // dto → entity (필요할 때만)
    private Voca dtoToEntity(VocaDTO dto) {
        Voca voca = new Voca();
        voca.setVocaId(dto.getVocaId());
        voca.setVocabulary(dto.getVocabulary());
        voca.setDescription(dto.getDescription());
        voca.setCreateDate(dto.getCreateDate());
        voca.setIncNoteList(dto.getIncNoteList());
        return voca;
    }

    public List<VocaDTO> getSelectAll() {
        List<Voca> entityList = vocaRepository.findAll();
        List<VocaDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            Voca voca = entityList.get(i);
            VocaDTO dto = entityToDto(voca);
            dtoList.add(dto);
        }

        return dtoList;
    }

    public VocaDTO getSelectOne(VocaDTO vocaDTO) {
        Optional<Voca> ov = vocaRepository.findById(vocaDTO.getVocaId());

        if (!ov.isPresent()) {
            return null;
        }

        Voca voca = ov.get();
        return entityToDto(voca);
    }

    public void setInsert(VocaDTO vocaDTO){
        Voca voca = dtoToEntity(vocaDTO);
        voca.setCreateDate(LocalDateTime.now());
        vocaRepository.save(voca);
    }

    public void setUpdate(VocaDTO vocaDTO) {
        Optional<Voca> ov = vocaRepository.findById(vocaDTO.getVocaId());
        if (ov.isPresent()) {
            Voca voca = ov.get();
            voca.setVocabulary(vocaDTO.getVocabulary());
            voca.setDescription(vocaDTO.getDescription());
            voca.setIncNoteList(vocaDTO.getIncNoteList());
            vocaRepository.save(voca);
        }
    }


    // 중복 방지 랜덤 문제 출제
    private List<Voca> getRandomVocasInternal(List<Integer> excludeIds, int count) {
        int excludeSize = (excludeIds == null) ? 0 : excludeIds.size();
        if (excludeIds == null || excludeIds.isEmpty()) {
            excludeIds = List.of(0);
        }
        return vocaRepository.findRandomVocas(excludeIds, excludeSize, count);
    }

    // 학습 1번 (단어 맞추기)
    public List<VocaDTO> getFillBlank(List<Integer> excludeIds, int count) {
        List<Voca> vocas = getRandomVocasInternal(excludeIds, count);
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
    public List<Map<String, Object>> getMultipleChoice(List<Integer> excludeIds, int count) {
        List<Map<String, Object>> questions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            List<Voca> options = getRandomVocasInternal(excludeIds, 4);

            Voca correct = options.get(0);
            String description = correct.getDescription();

            List<String> choices = new ArrayList<>();
            for (Voca v : options) {
                choices.add(v.getVocabulary());
            }
            Collections.shuffle(choices);

            Map<String, Object> question = new HashMap<>();
            question.put("vocaId", correct.getVocaId());
            question.put("description", description);
            question.put("options", choices);
            question.put("answer", correct.getVocabulary());

            questions.add(question);
        }
        return questions;
    }

 /*   // 학습 1번 (랜덤 출제)
    public List<VocaDTO> getCompletelyRandomFillBlank(int count) {
        List<Voca> vocas = vocaRepository.findByRandomVocas(count);
        List<VocaDTO> result = new ArrayList<>();

        for (Voca v : vocas) {
            VocaDTO dto = new VocaDTO();
            dto.setVocaId(v.getVocaId());
            dto.setVocabulary(v.getVocabulary());
            dto.setDescription(v.getDescription());
            dto.setCreateDate(v.getCreateDate());

            dto.setIncNoteList(null);

            result.add(dto);
        }

        return result;
    }

    // 학습 3번 (랜덤 출제)
    public List<Map<String, Object>> getCompletelyRandomMultipleChoice(int count) {
        List<Map<String, Object>> questions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            List<Voca> options = vocaRepository.findRandomVocas(Collections.emptyList(), 0, 4);

            Voca correct = options.get(0);
            String description = correct.getDescription();

            List<String> choices = new ArrayList<>();
            for (Voca v : options) {
                choices.add(v.getVocabulary());
            }
            Collections.shuffle(choices);

            Map<String, Object> question = new HashMap<>();
            question.put("vocaId", correct.getVocaId());
            question.put("description", description);
            question.put("options", choices);
            question.put("answer", correct.getVocabulary());

            questions.add(question);
        }

        return questions;
    }*/
}
