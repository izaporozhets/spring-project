package com.springproject.beautysaloon.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="speciality")
public class Speciality {
    @Id
    private Long id;
    @Column(name = "name")
    private String name;
}
