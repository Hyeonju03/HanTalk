package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {
    private int commentId;
    private int postId;
    private int userNo;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;



}
