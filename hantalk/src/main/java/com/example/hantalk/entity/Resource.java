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
    private int resource_id;     // RESOURCE_ID

    @Column(length = 200)
    private String title;        // TITLE

    @Column(columnDefinition = "TEXT")
    private String content;      // CONTENT

    @Column(length = 200)
    private String archive;      // ARCHIVE

    private int view_count;       // VIEW_COUNT

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime create_date;  // CREATE_DATE

    @CreatedDate
    private LocalDateTime update_date;  // UPDATE_DATE
}