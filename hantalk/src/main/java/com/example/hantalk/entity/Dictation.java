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
public class Dictation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dictation_id;

    @Column(length = 200)
    private String audio;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime create_date;
}
