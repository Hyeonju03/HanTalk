package com.example.hantalk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoUpdateDTO { // 수정용
    private Long video_id;
    private String title;
    private String content;
    private String video_name;
}

