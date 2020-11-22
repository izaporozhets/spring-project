package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface WorkDayRepository extends JpaRepository<WorkDay, Long> {
    @Query(value = "select workday from WorkDay workday where workday.masterId = ?1 and workday.day >= CURRENT_DATE ")
    List<WorkDay> findAllByMasterId(Long id);

    @Query("select workday.fromTime from WorkDay workday where workday.masterId = ?1 and workday.day = ?2")
    Time findStartTimeByMasterId(Long id, Date date);

    @Query("select workday.tillTime from WorkDay workday where workday.masterId = ?1 and workday.day = ?2")
    Time findEndTimeByMasterId(Long id, Date date);

    WorkDay findWorkDayByDayAndMasterId(Date date, Long id);

}
