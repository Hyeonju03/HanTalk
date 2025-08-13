package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class})
@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_id")
    private int commentId; // 아이디

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 내용

    @CreatedDate
    @Column(name="create_date", updatable = false, nullable = false)
    private LocalDateTime createDate; // 작성일

    @CreatedDate
    @Column(name="update_date", nullable = false)
    private LocalDateTime updateDate; // 수정일

    //fk
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 게시글

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private Users users; // 작성자
}