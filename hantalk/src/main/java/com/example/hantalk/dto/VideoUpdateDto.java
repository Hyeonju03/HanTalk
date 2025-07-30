package com.example.hantalk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoUpdateDto { // 수정용
    private Long id;
    private String title;
    private String content;
    private String videoName;
}

