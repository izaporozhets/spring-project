package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

    @Query("select procedure from Procedure procedure, SpecialityMaster specMaster where procedure.speciality.id = specMaster.specialityId")
    List<Procedure> findAllByMasterId(Long masterId);

    @Query("select procedure from Procedure procedure group by procedure.name")
    List<Procedure> findAllWithIdentityName();

}
