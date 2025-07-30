package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class})
@Getter
@Setter
@Entity
public class Inc_Note {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int inc_note_id;

    @CreatedDate
    @Column (name = "create_date", updatable = false)
    private LocalDateTime create_date;

    @ManyToOne
    @JoinColumn (name = "user_no")
    private User user;

    @ManyToOne
    @JoinColumn (name = "voca_id")
    private Voca voca;

    @ManyToOne
    @JoinColumn (name = "sentence_id")
    private Sentence sentence;

}
