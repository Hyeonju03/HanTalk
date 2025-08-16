package com.example.hantalk.controller;

import com.example.hantalk.dto.SentenceDTO;
import com.example.hantalk.service.SentenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sentence")
public class SentenceApiController {

    private final SentenceService sentenceService;

    @GetMapping(path="/one", produces="application/json")
    public SentenceDTO getOne() {
        //모든 db에 있는 문장 뽑아오면 될듯 허이 근데 찬우님이 나한테 설명을 해줘야함
        return null;
    }
}
