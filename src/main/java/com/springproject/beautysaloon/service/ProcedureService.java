package com.springproject.beautysaloon.service;

import com.springproject.beautysaloon.model.Procedure;

import java.util.List;
import java.util.Optional;

public interface ProcedureService {
    List<Procedure> findAll();
    List<Procedure> findAllByMasterId(Long masterId);
    Optional<Procedure> findById(Long id);
    List<Procedure> findAllWithIdentityName();
}
