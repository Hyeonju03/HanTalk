package com.example.hantalk.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {

    private Long video_id;
    private String title;
    private String content;
    private String video_name;
    private LocalDateTime create_date;
    private LocalDateTime update_date;
}