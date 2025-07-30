package com.example.hantalk.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @Column
    private int categoryId; //구분

    @Column
    private String categoryName; //카테고리명
}
