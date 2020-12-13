package com.springproject.beautysaloon.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String message;

    @Column(name = "rate")
    private Integer rate;

    @JoinColumn(name = "request_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Request request;

    @Override
    public String toString(){
        return "Procedure : " + request.getProcedure().getName() + " | Master : " + request.getMaster().getName();
    }

}
