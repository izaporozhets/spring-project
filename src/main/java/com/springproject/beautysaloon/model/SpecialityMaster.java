package com.springproject.beautysaloon.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "masters_specialities")
public class SpecialityMaster {

    @Id
    @Column(name = "master_id")
    private Long masterId;

    @Column(name = "speciality_id")
    private Long specialityId;
}
