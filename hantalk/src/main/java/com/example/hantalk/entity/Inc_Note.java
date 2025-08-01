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
    @Column(name="inc_note_id")
    private int incNoteId;

    @CreatedDate
    @Column(name="create_date", updatable = false)
    private LocalDateTime createDate;

    //fk

    @ManyToOne
    @JoinColumn (name = "user_no")
    private Users users;

    @ManyToOne
    @JoinColumn (name = "voca_id")
    private Voca voca;

    @ManyToOne
    @JoinColumn (name = "sentence_id")
    private Sentence sentence;
}
