package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SentenceDTO {
    private int sentence_id;
    private String munjang;
    private String explain;
    private LocalDateTime create_date;
}
