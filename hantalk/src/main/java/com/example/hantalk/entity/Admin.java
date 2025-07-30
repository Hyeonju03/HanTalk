package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Admin {
    // id랑 pwd만 있어도 됨. 혹시몰라 추가 되어있음.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int admin_no;

    @Column(nullable = false)
    private String admin_id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;


}
