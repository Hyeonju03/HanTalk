package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@EntityListeners(value = {AuditingEntityListener.class})
@Entity
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="video_id")
    private int videoId;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name="video_name", length = 200)
    private String videoName;

    @CreatedDate
    @Column(name="create_date", updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name="update_date")
    private LocalDateTime updateDate;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewHit = 0;

    @OneToMany(mappedBy = "video", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Favorite_video> favoriteVideoList;
}