package com.example.hantalk.dto;

import com.example.hantalk.entity.Post;
import com.example.hantalk.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {
    private int comment_id;
    private String content;
    private LocalDateTime create_date;
    private LocalDateTime update_date;

    private Post post;
    private Users users;
}
