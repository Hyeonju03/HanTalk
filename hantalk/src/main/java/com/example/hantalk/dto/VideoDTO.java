package com.example.hantalk.dto;

import com.example.hantalk.entity.Favorite_video;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class VideoDTO { //등록용
    private int videoId;
    private String title;
    private String content;
    private String videoName;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private int viewHit;
    private List<Favorite_video> favoriteVideoList;
}