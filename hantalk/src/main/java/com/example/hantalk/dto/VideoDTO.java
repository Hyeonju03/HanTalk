package com.example.hantalk.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class VideoDTO { //등록용
    private int videoId;
    private String title;
    private String content;
    private String videoName;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}