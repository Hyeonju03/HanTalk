package com.example.hantalk.dto;

import com.example.hantalk.entity.Post;
import com.example.hantalk.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {
    private int commentId;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    private Post post;
    private Users users;

    public void setPostId(int postId) {

    }
}
