package com.example.hantalk.dto;

import com.example.hantalk.entity.Category;
import com.example.hantalk.entity.Comment;
import com.example.hantalk.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostDTO {
    private int postId;
    private String title;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String content;
    private String archive;
    private int viewCount;

    private Users users;
    private Category category;

    private List<Comment> commentList;
}
