package com.springproject.beautysaloon.service.impl;

import com.springproject.beautysaloon.model.Procedure;
import com.springproject.beautysaloon.repository.ProcedureRepository;
import com.springproject.beautysaloon.service.ProcedureService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProcedureServiceImpl implements ProcedureService {

    private final ProcedureRepository procedureRepository;

    public ProcedureServiceImpl(ProcedureRepository procedureRepository){
        this.procedureRepository = procedureRepository;
    }

    @Override
    public List<Procedure> findAll() {
        return procedureRepository.findAll();
    }

    @Override
    public List<Procedure> findAllByMasterId(Long masterId) {
        return procedureRepository.findAllByMasterId(masterId);
    }

    @Override
    public Optional<Procedure> findById(Long id) {
        return procedureRepository.findById(id);
    }

    @Override
    public List<Procedure> findAllWithIdentityName() {
        return procedureRepository.findAllWithIdentityName();
    }
}
