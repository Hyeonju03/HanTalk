package com.example.hantalk.dto;

import com.example.hantalk.entity.Sentence;
import com.example.hantalk.entity.Users;
import com.example.hantalk.entity.Voca;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Inc_NoteDTO {
    private int inc_note_id;
    private LocalDateTime create_date;

    private Users users;
    private Voca voca;
    private Sentence sentence;
}
