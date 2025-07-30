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
    private int category_id; //구분

    @Column(length = 200)
    private String category_name; //카테고리명

    /// /////////////////////////////////
    @OneToMany(mappedBy = "category", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Post> post_list;
}
