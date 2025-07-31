package com.example.hantalk.dto;

import com.example.hantalk.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceDTO {
    private int attendanceId;
    private LocalDateTime attendDate;

    private Users users;
}
