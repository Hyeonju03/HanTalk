package com.example.hantalk.dto;

import com.example.hantalk.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LogDataDTO {
    private int id;
    private UsersDTO user;
    private String uri;
    private String method;
    private String device;
    private long responseTime;
    private int statusCode;
    private LocalDateTime createDate;
}
