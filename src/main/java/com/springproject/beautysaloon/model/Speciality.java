package com.springproject.beautysaloon.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="speciality")
public class Speciality{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
