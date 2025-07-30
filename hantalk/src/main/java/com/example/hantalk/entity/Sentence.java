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
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sentence_id;

    @Column(length = 200)
    private String munjang;

    @Column(columnDefinition = "TEXT")
    private String explain;

    @CreatedDate
    private LocalDateTime create_date;
}