package com.springproject.beautysaloon.service.impl;

import com.springproject.beautysaloon.model.Request;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.repository.RequestRepository;
import com.springproject.beautysaloon.service.RequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public Page<Request> findPaginatedMaster(int pageNo, int pageSize, String sortField, String sortDirection, Long masterId){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return requestRepository.findAllByMasterId(pageable, masterId);
    }

    @Override
    public Page<Request> findPaginatedClient(int pageNo, int pageSize, String sortField, String sortDirection, User client) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo -1 , pageSize, sort);
        return requestRepository.findAllByClient(pageable, client);
    }

    @Override
    public Page<Request> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return requestRepository.findAll(pageable);
    }

    @Override
    public List<Request> findAllRequestsByMasterIdAndDate(Long id, Timestamp timestamp) {
        return requestRepository.findAllRequestsByMasterIdAndDate(id, timestamp);
    }

    @Override
    public List<Request> findAllByMasterId(Long id) {
        return requestRepository.findAllByMasterId(id);
    }

    @Override
    public void saveRequest(Request request) {
        requestRepository.save(request);
    }
}
