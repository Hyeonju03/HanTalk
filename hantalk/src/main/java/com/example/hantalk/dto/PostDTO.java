package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDTO {

    private int postId;
    private  int categoryId;
    private String title;
    private int userNo;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String content;
    private String archive;
    private int viewCount;


}
