package com.example.hantalk.dto;

import com.example.hantalk.entity.Video;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponseDto { //응답용 (상세/목록 둘 다 가능)

    private Long id;
    private String title;
    private String content;
    private String videoName;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public VideoResponseDto(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.content = video.getContent();
        this.videoName = video.getVideoName();
        this.createDate = video.getCreateDate();
        this.updateDate = video.getUpdateDate();
    }
}