package com.example.hantalk.service;

import com.example.hantalk.dto.SentenceDTO;
import com.example.hantalk.entity.Sentence;
import com.example.hantalk.repository.SentenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // 페이징 및 검색 기능이 포함된 메소드
    public Page<SentenceDTO> getSelectAll(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        Page<Sentence> entityPage;
        if (kw == null || kw.isEmpty()) {
            entityPage = repository.findAll(pageable);
        } else {
            entityPage = repository.findByMunjangContainingOrDescriptionContaining(kw, kw, pageable);
        }
        return entityPage.map(this::entityToDto);
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

    public void setUpdate(SentenceDTO dto) {
        System.out.println(">>> setUpdate 메소드 시작"); // 로그 추가
        Optional<Sentence> m = repository.findById(dto.getSentenceId());
        if(m.isPresent()){
            Sentence sentence = m.get();
            // 업데이트할 내용 설정
            sentence.setMunjang(dto.getMunjang());
            sentence.setDescription(dto.getDescription());
            repository.save(sentence);
            System.out.println(">>> 문장 ID " + sentence.getSentenceId() + " 업데이트 성공"); // 로그 추가
        } else {
            System.out.println(">>> 문장 ID " + dto.getSentenceId() + "를 찾을 수 없음. 업데이트 실패"); // 로그 추가
        }
    }

    public void setDelete(SentenceDTO sentenceDTO){
        repository.deleteById(sentenceDTO.getSentenceId());
    }

    // ++ 오답노트 처리를 위한 엔티티 직접 반환 메서드
    public Sentence getSelectRandomEntity(){
        Optional<Sentence> os = repository.findByRandom();

        return os.orElse(null);
    }
}
