package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Inc_NoteDTO {

    private int incNoteId;
    private LocalDateTime createDate;

    private UserDTO user;

    private VocaDTO voca;

    private SentenceDTO sentence;
}
