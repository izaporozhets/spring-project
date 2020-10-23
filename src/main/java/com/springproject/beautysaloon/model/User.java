package com.springproject.beautysaloon.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="rating")
    private int rating;
    @Column(name ="speciality_id")
    private int specialityId;

    @Column(name ="visits")
    private int visits;
    @Column(name ="email")
    private String email;
    @Column(name ="full_name")
    private String name;
    @Column(name ="password")
    private String password;
    @Enumerated(value = EnumType.STRING)
    @Column(name ="role")
    private Role role;
    @Enumerated(value = EnumType.STRING)
    @Column(name ="status")
    private Status status;
}
