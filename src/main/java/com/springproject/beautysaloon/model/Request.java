package com.springproject.beautysaloon.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Data
@Entity
@Table(name ="request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "procedure_id" )
    @ManyToOne(fetch = FetchType.LAZY)
    private Procedure procedure;

    @Column(name = "date")
    private Timestamp date;

    @Column(name = "time")
    private Time time;

    @JoinColumn(name = "client_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User client;

    @JoinColumn(name = "master_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User master;

    @Column(name = "done")
    private boolean done;

    @Override
    public String toString(){
        return String.valueOf(getId());
    }

}
