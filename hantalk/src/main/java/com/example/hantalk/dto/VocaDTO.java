package com.example.hantalk.dto;

import com.example.hantalk.entity.Inc_Note;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class VocaDTO {
    private int vocaId;
    private String vocabulary;
    private String description;
    private LocalDateTime createDate;

    private List<Inc_Note> incNoteList;
}
