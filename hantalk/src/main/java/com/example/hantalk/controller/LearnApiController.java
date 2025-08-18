package com.example.hantalk.controller;

import com.example.hantalk.dto.SentenceDTO;
import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.service.SentenceService;
import com.example.hantalk.service.VocaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learn")
public class LearnApiController {

    private final VocaService vocaService;
    private final SentenceService sentenceService;

    // 단어 or 문장만 선택 가능
    @GetMapping(path="/one", produces="application/json")
    public Map<String, Object> getOne(
            @RequestParam(name="kind", required=false, defaultValue="word") String kind) {
        if ("word".equals(kind)) {
            return fromVoca(pickWord());
        } else if ("sentence".equals(kind)) {
            return fromSentence(pickSentence());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "항목이 없습니다.");
        }
    }

    private VocaDTO pickWord() {
        List<VocaDTO> list = vocaService.getFillBlank(Collections.emptyList(), 1);
        if (list.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "단어가 없습니다.");
        return list.get(0);
    }

    private SentenceDTO pickSentence() {
        SentenceDTO s = sentenceService.getSelectRandom();
        if (s == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "문장이 없습니다.");
        return s;
    }

    private Map<String, Object> fromVoca(VocaDTO vocaDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "WORD");
        map.put("text", vocaDTO.getVocabulary());
        map.put("mean", vocaDTO.getDescription());
        return map;
    }

    private Map<String, Object> fromSentence(SentenceDTO sentenceDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "SENTENCE");
        map.put("text", sentenceDTO.getMunjang());
        map.put("mean", sentenceDTO.getDescription());
        return map;
    }

    @GetMapping(path="/both", produces="application/json")
    public Map<String, Object> getBoth() {
        VocaDTO v = pickWord();
        SentenceDTO s = pickSentence();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("vocabulary", v.getVocabulary());
        map.put("descriptionWord", v.getDescription());
        map.put("munjang", s.getMunjang());
        map.put("descriptionMunjang", s.getDescription());
        return map;
    }

}
