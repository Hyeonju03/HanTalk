package com.example.hantalk.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
    @Column(name="post_id")
    private int postId; //구분용

    @Column(length = 200, nullable = false)
    private String title; //제목

    @CreatedDate
    @Column(name="create_date", updatable = false, nullable = false)
    private LocalDateTime createDate; //작성일

    @CreatedDate
    @Column(name="update_date", nullable = false)
    private LocalDateTime updateDate; //수정일

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; //본문

    @Column(length = 200)
    private String archive; //첨부파일명과 타입

    @Column(name="view_count")
    private int viewCount = 0; // 조회수

    //fk

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private Users users; // 작성자

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; //게시판 구분용

    ///////////////////////////////////////////

    @OneToMany(mappedBy = "post", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    @Column(name="comment_list")
    private List<Comment> commentList;

}
