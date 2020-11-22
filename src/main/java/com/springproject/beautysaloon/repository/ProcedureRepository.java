package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

    @Query("select procedure from Procedure procedure where procedure.master.id = ?1")
    List<Procedure> findAllByMasterId(Long masterId);

    @Query("select procedure from Procedure procedure group by procedure.name")
    List<Procedure> findAllWithIdentityName();

}
