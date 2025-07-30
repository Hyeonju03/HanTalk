package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VocaDTO {

    private int vocaId;
    private String vocabulary;
    private String explain;
    private LocalDateTime createDate;

}
