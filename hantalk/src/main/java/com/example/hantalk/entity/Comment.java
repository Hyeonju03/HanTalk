package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_id; // 아이디

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 내용

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime create_date; // 작성일

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime update_date; // 수정일

    //fk
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 게시글

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private User user; // 작성자
}

