package com.example.hantalk.dto;

import com.example.hantalk.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminDTO {
    private int attendance_id;
    private LocalDateTime attend_date;

    private Users users;
}
