package com.example.hantalk.dto;

import com.example.hantalk.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryDTO {
    private int category_id;
    private String category_name;

    private List<Post> post_list;
}
