package com.example.hantalk.controller;

import com.example.hantalk.dto.VocaDTO;
import com.example.hantalk.service.VocaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/voca")
public class VocaApiController {

    private final VocaService vocaService;

    @GetMapping(path="/one", produces="application/json")
    public VocaDTO getOne() {

        List<VocaDTO> list = vocaService.getFillBlank(Collections.emptyList(), 1);
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no data");
        }
        return list.get(0);
    }
}
