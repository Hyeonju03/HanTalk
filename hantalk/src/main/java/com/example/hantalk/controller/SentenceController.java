package com.example.hantalk.controller;

import com.example.hantalk.service.SentenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/study")
@Controller
public class SentenceController {
    private final SentenceService sentenceService;
    String folderName = "study";

    @GetMapping("/")
    public String

    //일단 받아쓰기 / 빈칸 만드는 메서드 각각 필요하고, 답을 받아서 판단하는 처리 메서드도 필요함
}
