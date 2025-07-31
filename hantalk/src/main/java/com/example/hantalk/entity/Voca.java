package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(value = {AuditingEntityListener.class})
public class Voca {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="voca_id")
    private int vocaId;

    @Column (nullable = false)
    private String vocabulary;

    @Column (nullable = false, columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column (name="create_date", nullable = false)
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "voca", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    @Column(name="inc_note_list")
    private List<Inc_Note> incNoteList;
}
