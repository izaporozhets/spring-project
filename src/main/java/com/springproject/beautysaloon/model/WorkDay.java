package com.springproject.beautysaloon.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "constant_schedule")
public class WorkDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "master_id")
    private Long masterId;
    @Column(name = "weekday")
    private Date day;
    @Column(name = "from_time")
    private Time fromTime;
    @Column(name = "till_time")
    private Time tillTime;

}
