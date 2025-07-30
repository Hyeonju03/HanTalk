package com.example.hantalk.dto;

import com.example.hantalk.entity.Users;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
public class Leaning_LogDTO {
    private int learning_id;
    private LocalDateTime learning_date;
    private int learning1_count = 0;
    private int learning2_count = 0;
    private int learning3_count = 0;
    private int learning4_count = 0;

    private Users users;
}
