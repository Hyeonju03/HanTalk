package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class LogData {//예약어랑 충돌날까봐 변경
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_no", nullable = false)
    private int userNo;

    private String uri;
    private String method;
    private String device;
    private long responseTime;
    private int statusCode;
    private LocalDateTime createDate;
}

