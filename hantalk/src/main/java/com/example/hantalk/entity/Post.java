package com.example.hantalk.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@EntityListeners(value = {AuditingEntityListener.class})
@Entity
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int post_id; //구분용

    @Column(length = 200, nullable = false)
    private String title; //제목

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime create_date; //작성일

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime update_date; //수정일

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; //본문

    @Column(length = 200)
    private String archive; //첨부파일명과 타입

    private int view_count = 0; // 조회수

    //fk

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private Users users; // 작성자

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; //게시판 구분용

    ///////////////////////////////////////////

    @OneToMany(mappedBy = "post", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Comment> comment_list;

}
