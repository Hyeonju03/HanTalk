package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LogDataDTO {
    private int id;
    private int userNo;
    private String uri;
    private String method;
    private String device;
    private long responseTime;
    private int statusCode;
    private LocalDateTime createDate;
}
