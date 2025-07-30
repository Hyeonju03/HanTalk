package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resource_id;     // RESOURCE_ID


    private String title;        // TITLE

    @Column(columnDefinition = "TEXT")
    private String content;      // CONTENT

    private String archive;      // ARCHIVE

    private int view_count;       // VIEW_COUNT

    private LocalDateTime create_date;  // CREATE_DATE

    private LocalDateTime update_date;  // UPDATE_DATE

    // + 생성자, getter/setter 등 필요시 추가
}