package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "VIDEO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VIDEO_ID")
    private Long video_id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "VIDEO_NAME")
    private String video_name;

    @Column(name = "CREATE_DATE", updatable = false)
    private LocalDateTime create_date;

    @Column(name = "UPDATE_DATE")
    private LocalDateTime update_date;

    @PrePersist
    protected void onCreate() {
        this.create_date = LocalDateTime.now();
        this.update_date = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.update_date = LocalDateTime.now();
    }
}