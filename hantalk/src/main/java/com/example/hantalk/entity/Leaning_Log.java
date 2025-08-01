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
    @Column(name="learning_id")
    private int learningId;

    @CreatedDate
    @Column(name="learning_date", updatable = false)
    private LocalDateTime learningDate;

    @Column(name="learning1_count")
    private int learning1Count = 0; // 단어맞추기(단답형)
    @Column(name="learning2_count")
    private int learning2Count = 0; // 문장나열
    @Column(name="learning3_count")
    private int learning3Count = 0; // 단어맞추기(객관식)
    @Column(name="learning4_count")
    private int learning4Count = 0; // 받아쓰기

    //fk
    @ManyToOne
    @JoinColumn (name = "user_no")
    private Users users;
}
