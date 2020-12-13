package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.SpecialityMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MastersSpecialitiesRepository extends JpaRepository<SpecialityMaster, Long> {
    @Query("select id.specialityId from SpecialityMaster id where id.masterId = ?1")
    Long getSpecialityIdByMasterId(Long id);
}
