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
    @Column(name="admin_no")
    private int adminNo;

    @Column(name="admin_id", nullable = false)
    private String adminId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;


}
