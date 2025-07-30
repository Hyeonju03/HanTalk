package com.example.hantalk.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "post")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId; //구분용

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; //게시판 구분용

    @Column(nullable = false)
    private String title; //제목

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private User user; // 작성자

    @Column(nullable = false)
    private LocalDateTime createDate; //작성일

    @Column(nullable = false)
    private LocalDateTime updateDate; //수정일

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; //본문

    @Column
    private String archive; //첨부파일명과 타입

    @Column
    private int viewCount = 0; // 조회수

}
