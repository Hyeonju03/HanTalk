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
    private int post_id;
    private String title;
    private LocalDateTime create_date;
    private LocalDateTime update_date;
    private String content;
    private String archive;
    private int view_count;

    private Users users;
    private Category category;

    private List<Comment> comment_list;
}
