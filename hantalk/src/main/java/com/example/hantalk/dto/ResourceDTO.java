package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResourceDTO {
    private Long resourceId;
    private String title;
    private String content;
    private String archive;

    private int viewCount = 0;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

}
