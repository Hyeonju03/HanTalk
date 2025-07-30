package com.example.hantalk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;     // RESOURCE_ID

    private String title;        // TITLE

    @Column(columnDefinition = "TEXT")
    private String content;      // CONTENT

    private String archive;      // ARCHIVE

    private int viewCount;       // VIEW_COUNT

    private LocalDateTime createDate;  // CREATE_DATE

    private LocalDateTime updateDate;  // UPDATE_DATE

    // + 생성자, getter/setter 등 필요시 추가
}