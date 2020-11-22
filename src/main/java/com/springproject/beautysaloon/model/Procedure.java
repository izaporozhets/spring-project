package com.springproject.beautysaloon.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "`procedure`")
public class Procedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;

    @Column(name = "`procedure_name`")
    private String name;

    @Column(name = "`cost`")
    private float cost;

    @Column(name = "`duration`")
    private Time duration;

    @JoinColumn(name = "`master_id`")
    @ManyToOne(fetch = FetchType.LAZY)
    private User master;

    @Override
    public String toString(){
        return name;
    }

}
