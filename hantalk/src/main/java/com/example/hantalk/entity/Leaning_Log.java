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

    //fk
    private int user_no;

    @CreatedDate
    private LocalDateTime learning_date;

    private int learning1_count;
    private int learning2_count;
    private int learning3_count;
    private int learning4_count;
}
