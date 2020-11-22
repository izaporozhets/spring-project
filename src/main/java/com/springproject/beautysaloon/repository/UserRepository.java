package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllPaginatedByRole(Pageable var1, Role role);
    List<User> findAllByRole(Role role);
    Optional<User> findByEmail(String email);
    @Query(value = "select user from User user, Procedure procedure where procedure.name = ?1 and procedure.master.id = user.id")
    List<User> findAllMastersByProcedureName(String name);
    @Query(value = "select user from User user, WorkDay workday where workday.day = ?1 and user.id = workday.masterId")
    List<User> findAllByDate(Date date);
    @Query(value = "select user.rating from User user where user.id = ?1")
    Integer findRatingByMasterId(Long id);

}