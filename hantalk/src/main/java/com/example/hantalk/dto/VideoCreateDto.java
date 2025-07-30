package com.example.hantalk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoCreateDto { //등록용
    private String title;
    private String content;
    private String videoName;
}