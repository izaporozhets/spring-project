package com.springproject.beautysaloon.service;

import com.springproject.beautysaloon.model.Request;
import com.springproject.beautysaloon.model.User;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;
public interface RequestService {
    Page<Request> findPaginatedMaster(int pageNo, int pageSize, String sortField, String sortDirection, Long masterId);
    Page<Request> findPaginatedClient(int pageNo, int pageSize, String sortField, String sortDirection, User client);
    Page<Request> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
    List<Request> findAllRequestsByMasterIdAndDate(Long id, Timestamp date);
    List<Request> findAllByMasterId(Long id);
    void saveRequest(Request request);
}
