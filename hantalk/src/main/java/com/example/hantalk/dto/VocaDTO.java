package com.example.hantalk.dto;

import com.example.hantalk.entity.Inc_Note;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class VocaDTO {
    private int voca_id;
    private String vocabulary;
    private String explain;
    private LocalDateTime create_date;

    private List<Inc_Note> inc_note_list;
}
