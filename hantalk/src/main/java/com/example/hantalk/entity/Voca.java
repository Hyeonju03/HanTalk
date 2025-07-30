package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class
Voca {

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

}
