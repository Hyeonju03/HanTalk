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
public class Leaning_Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int learning_id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime learning_date;

    private int learning1_count = 0; // 단어맞추기(단답형)
    private int learning2_count = 0; // 문장나열
    private int learning3_count = 0; // 단어맞추기(객관식)
    private int learning4_count = 0; // 받아쓰기

    //fk
    @ManyToOne
    @JoinColumn (name = "user_no")
    private Users users;
}
