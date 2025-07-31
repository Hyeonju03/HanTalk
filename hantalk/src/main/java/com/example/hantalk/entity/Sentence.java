package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@EntityListeners(value = {AuditingEntityListener.class})
@Entity
@Getter
@Setter
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sentence_id")
    private int sentenceId;

    @Column(length = 200)
    private String munjang;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(name="create_date", updatable = false)
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "sentence", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    @Column(name="inc_note_list")
    private List<Inc_Note> incNoteList;
}