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
    private int voca_id;

    @Column (nullable = false)
    private String vocabulary;

    @Column (nullable = false, columnDefinition = "TEXT")
    private String explain;

    @CreatedDate
    @Column (nullable = false)
    private LocalDateTime create_date;

    @OneToMany(mappedBy = "voca", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Inc_Note> inc_note_list;
}
