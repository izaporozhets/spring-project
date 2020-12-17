package com.springproject.beautysaloon.repository;

import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByRole(Role role);

    Page<User> findAllPaginatedByRole(Pageable var1, Role role);

    List<User> findAllByRole(Role role);

    Optional<User> findByEmail(String email);

    @Query(value = "select user from User user, Speciality speciality, SpecialityMaster specMaster where speciality.id = ?1 and specMaster.specialityId = speciality.id and specMaster.masterId = user.id")
    List<User> findAllMastersBySpecialityId(Long id);

    @Query(value = "select user from User user, WorkDay workday where workday.day = ?1 and user.id = workday.masterId")
    List<User> findAllByDate(Date date);

    @Query(value = "select user.rating from User user where user.id = ?1")
    Integer findRatingByMasterId(Long id);

    @Transactional
    @Modifying
    @Query(value = "update User user set user.visits = user.visits + 1 where user.id = ?1")
    void incrementVisitsById(Long id);

    @Transactional
    @Modifying
    @Query(value = "update User user set user.visits = user.visits - 1 where user.id = ?1")
    void decrementVisitsById(Long id);

}