package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.Request;
import com.springproject.beautysaloon.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findAllByClient(Pageable pageable, User client);

    @Query("select request from Request request where request.procedure.master.id =?1")
    Page<Request> findAllByMasterId(Pageable pageable, Long masterId);

    @Query("select request from Request request where request.procedure.master.id = ?1 and request.date = ?2")
    List<Request> findAllRequestsByMasterIdAndDate(Long id, Timestamp date);

    @Query("select request from Request request where  request.procedure.master.id = ?1")
    List<Request> findAllByMasterId(Long id);

    @Query("select request.client from Request request where request.procedure.master.id = ?1 group by request.client.name")
    List<User> findAllClientsByMasterId(Long id);

}
