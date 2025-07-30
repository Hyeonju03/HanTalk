package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId; // 아이디

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 게시글

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private User user; // 작성자

    @Column(nullable = false)
    private String content; // 내용

    @Column(nullable = false)
    private LocalDateTime createDate; // 작성일

    @Column(nullable = false)
    private LocalDateTime updateDate; // 수정일
}

