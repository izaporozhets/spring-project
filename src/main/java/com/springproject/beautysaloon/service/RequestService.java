package com.springproject.beautysaloon.service;

import com.springproject.beautysaloon.model.Request;
import com.springproject.beautysaloon.model.User;
import org.springframework.data.domain.Page;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RequestService {

    Page<Request> findPaginatedByPersonId(int pageNo, int pageSize, String sortField, String sortDirection, Long clientId);

    Page<Request> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

    List<Request> findAllRequestsByMasterIdAndDate(Long id, Timestamp date);

    List<Request> findAllByMasterId(Long id);

    Page<Request> findAllByClientId(int pageNo, int pageSize, String sortField, String sortDirection, Long id);

    void saveRequest(Request request);

    Optional<Request> findById(Long id);

    List<Time> getTimeSlots(Long masterId, String timestamp, Date selectedProcedureDuration);

    List<String> getWorkingDays(Long masterId);

    void setDone(Long id);

    void deleteById(Long id);
}
