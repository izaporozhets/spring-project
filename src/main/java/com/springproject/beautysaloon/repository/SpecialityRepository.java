package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {

}
