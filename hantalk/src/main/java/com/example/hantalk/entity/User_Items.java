package com.example.hantalk.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class User_Items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_item_id; // 구분용

    private LocalDateTime acquired_at; // 획득일

    @Column(nullable = false)
    private Boolean equipped = false; // 착용 여부 (기본값: false)

    //fk

    @ManyToOne
    @JoinColumn (name = "user_no")
    private Users users;

    @ManyToOne
    @JoinColumn (name = "item_id")
    private Item item;
}
