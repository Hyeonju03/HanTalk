package com.example.hantalk.dto;

import com.example.hantalk.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Leaning_LogDTO {
    private int learningId;
    private LocalDateTime learningDate;
    private int learning1Count = 0;
    private int learning2Count = 0;
    private int learning3Count = 0;
    private int learning4Count = 0;

    private Users users;
}
