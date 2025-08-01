package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DictationDTO {
    private int dictationId;
    private String audio;
    private String answer;
    private LocalDateTime createDate;
}
