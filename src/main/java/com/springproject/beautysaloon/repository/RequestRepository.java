package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.Request;
import com.springproject.beautysaloon.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findAllByClient_Id(Pageable pageable, Long id);

    @Query("select request from Request request where request.procedure.master.id = ?1")
    Page<Request> findAllByProcedureMaster(Pageable pageable, Long id);

    @Query("select request from Request request where request.procedure.master.id = ?1 and request.date = ?2")
    List<Request> findAllRequestsByMasterIdAndDate(Long id, Timestamp date);

    @Query("select request from Request request where  request.procedure.master.id = ?1")
    List<Request> findAllByMasterId(Long id);

    @Query("select request.client from Request request where request.procedure.master.id = ?1 group by request.client.name")
    List<User> findAllClientsByMasterId(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Request request \n" +
            "SET request.done = \n" +
            "  CASE request.done \n" +
            "    WHEN TRUE THEN FALSE \n" +
            "    ELSE TRUE END WHERE request.id = ?1")
    void setDone(Long id);

    void deleteById(Long id);
}
