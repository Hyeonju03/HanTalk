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
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="resource_id")
    private int resourceId;     // RESOURCE_ID

    @Column(length = 200)
    private String title;        // TITLE

    @Column(columnDefinition = "TEXT")
    private String content;      // CONTENT

    @Column(length = 200)
    private String archive;      // ARCHIVE

    @Column(name="view_count")
    private int viewCount;       // VIEW_COUNT

    @CreatedDate
    @Column(name="create_date", updatable = false)
    private LocalDateTime createDate;  // CREATE_DATE

    @CreatedDate
    @Column(name="update_date")
    private LocalDateTime updateDate;  // UPDATE_DATE
}