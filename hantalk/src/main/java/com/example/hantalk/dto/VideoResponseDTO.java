package com.example.hantalk.dto;

import com.example.hantalk.entity.Video;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponseDTO { //응답용 (상세/목록 둘 다 가능)

    private Long video_id;
    private String title;
    private String content;
    private String video_name;
    private LocalDateTime create_date;
    private LocalDateTime update_date;

    public VideoResponseDTO(Video video) {
        this.video_id = video.getId();
        this.title = video.getTitle();
        this.content = video.getContent();
        this.video_name = video.getVideoName();
        this.create_date = video.getCreateDate();
        this.update_date = video.getUpdateDate();
    }
}