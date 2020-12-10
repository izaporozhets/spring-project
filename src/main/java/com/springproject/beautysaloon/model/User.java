package com.springproject.beautysaloon.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name ="rating")
    private int rating;

    @ManyToMany
    @JoinTable(name = "masters_specialities",
            joinColumns = {@JoinColumn(name="master_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "speciality_id", referencedColumnName = "id")}
    )
    private List<Speciality> specialityList;

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

    @Override
    public String toString(){
        return getName();
    }


}
