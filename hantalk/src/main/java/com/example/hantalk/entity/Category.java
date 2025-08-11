package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId; //구분

    @Column(name = "category_name", length = 200)
    private String categoryName; //카테고리명

    /// /////////////////////////////////
    @OneToMany(mappedBy = "category", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    @Column(name = "post_list")
    private List<Post> postList;


}
