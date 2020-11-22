package com.springproject.beautysaloon.service;

import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.User;
import org.springframework.data.domain.Page;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Page<User> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, Role role);
    List<User> findAllMasters();
    List<User> findAllMastersByProcedureName(String name);
    Optional<User> findById(Long id);
    List<User> findAllByDate(Date date);
    Optional<User> findByEmail(String email);
    List<User> findAllClients();
    Optional<User> findByUsername(String username);
    Integer getMasterRatingById(Long id);
    void saveUser(User user);
    void deleteUser(Long id);
}

