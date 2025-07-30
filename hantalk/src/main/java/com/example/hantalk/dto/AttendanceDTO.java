package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceDTO {

    private Integer attendanceId;
    private Integer userNo;
    private LocalDateTime attendDate;

}
