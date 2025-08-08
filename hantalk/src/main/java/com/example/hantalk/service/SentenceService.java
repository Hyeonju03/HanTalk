package com.example.hantalk.service;

import com.example.hantalk.dto.SentenceDTO;
import com.example.hantalk.entity.Sentence;
import com.example.hantalk.repository.SentenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SentenceService {
    private final SentenceRepository repository;

    private SentenceDTO entityToDto(Sentence sentence){
        SentenceDTO dto = new SentenceDTO();

        dto.setSentenceId(sentence.getSentenceId());
        dto.setMunjang(sentence.getMunjang());
        dto.setDescription(sentence.getDescription());
        dto.setCreateDate(sentence.getCreateDate());
        dto.setIncNoteList(sentence.getIncNoteList());

        return dto;
    }

    private Sentence dtoToEntity(SentenceDTO dto){
        Sentence sentence = new Sentence();

        sentence.setSentenceId(dto.getSentenceId());
        sentence.setMunjang(dto.getMunjang());
        sentence.setDescription(dto.getDescription());
        sentence.setCreateDate(dto.getCreateDate());
        sentence.setIncNoteList(dto.getIncNoteList());

        return sentence;
    }

    public List<SentenceDTO> getSelectAll(){
        List<Sentence> entityList = repository.findAll();
        List<SentenceDTO> dtoList = new ArrayList<>();

        for(int i = 0; i < entityList.size(); i++){
            dtoList.add(entityToDto(entityList.get(i)));
        }

        return dtoList;
    }

    public SentenceDTO getSelectRandom(){
        Optional<Sentence> os = repository.findByRandom();

        if(!os.isPresent()){
            return null;
        }

        Sentence sentence = os.get();
        return entityToDto(sentence);
    }

    public SentenceDTO getSelectOne(SentenceDTO sentenceDTO){
        Optional<Sentence> os = repository.findById(sentenceDTO.getSentenceId());

        if(!os.isPresent()){
            return null;
        }

        Sentence sentence = os.get();
        return entityToDto(sentence);
    }

    public void setInsert(SentenceDTO sentenceDTO){
        Sentence sentence = dtoToEntity(sentenceDTO);
        sentence.setCreateDate(LocalDateTime.now());
        repository.save(sentence);
    }

    public void setUpdate(SentenceDTO sentenceDTO){
        Optional<Sentence> os = repository.findById(sentenceDTO.getSentenceId());
        if(os.isPresent()){
            Sentence sentence = os.get();
            sentence.setMunjang(sentenceDTO.getMunjang());
            sentence.setDescription(sentenceDTO.getDescription());
            sentence.setIncNoteList(sentenceDTO.getIncNoteList());
            repository.save(sentence);
        }
    }

    public void setDelete(SentenceDTO sentenceDTO){
        repository.deleteById(sentenceDTO.getSentenceId());
    }
}
