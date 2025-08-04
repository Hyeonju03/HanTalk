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
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int resourceId;

    private String title;

    @Column(length = 2000)
    private String content;

    @Column(name = "original_file_name")
    private String originalFileName;

    private String archive;

    private int viewCount;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}