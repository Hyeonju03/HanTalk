package com.example.hantalk.controller;

import com.example.hantalk.service.SentenceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/study")
@Controller
public class SentenceController {
    private static final Logger logger = LoggerFactory.getLogger(SentenceController.class);
    private final SentenceService sentenceService;
    String folderName = "study";

    @GetMapping("/lesson2")
    public String lesson2(Model model) {
        //문장나열
        String sentence = sentenceService.getSelectRandom().getMunjang();
        System.out.println(sentence);
        if(sentence != null) {
            String[] splitList = sentence.split("\\^");
            List<String> shuffleList = Arrays.asList(splitList);
            Collections.shuffle(shuffleList);
            System.out.println(shuffleList);
            model.addAttribute("shuffleList", shuffleList);
        }
        model.addAttribute("sentence", sentence);
        return folderName + "/lesson2";
    }

    //받아쓰기
    @GetMapping("/lesson4")
    public String lesson4() {
        return folderName + "/lesson4";
    }

    //random문장 가져오기
    @GetMapping("/api/get-random-sentence")
    @ResponseBody
    public Map<String, String> getRandomSentence() {
        String sentence = sentenceService.getSelectRandom().getMunjang();

        if (sentence != null) {
            sentence = sentence.replace("^", " ");
        } else {
            return Collections.singletonMap("sentence", "문장을 불러올 수 없습니다.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("sentence", sentence);
        return response;
    }

    @PostMapping("/api/check-answer")
    @ResponseBody
    public Map<String, Object> checkAnswer(@RequestBody Map<String, String> payload) {
        String originalSentence = payload.get("originalSentence");
        String userAnswer = payload.get("userAnswer");

        logger.info("Original Sentence: {}", originalSentence);
        logger.info("User Answer: {}", userAnswer);

        boolean isCorrect = originalSentence.equals(userAnswer);

        Map<String, Object> response = new HashMap<>();
        response.put("isCorrect", isCorrect);

        return response;
    }
}
