package com.springproject.beautysaloon.service.impl;

import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.repository.ProcedureRepository;
import com.springproject.beautysaloon.repository.UserRepository;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProcedureRepository procedureRepository;

    public UserServiceImpl(UserRepository userRepository, ProcedureRepository procedureRepository) {
        this.userRepository = userRepository;
        this.procedureRepository = procedureRepository;
    }

    @Override
    public Page<User> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, Role role) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return userRepository.findAllPaginatedByRole(pageable, role);
    }

    @Override
    public List<User> findAllMasters() {
        return userRepository.findAllByRole(Role.MASTER);
    }

    @Override
    public List<User> findAllMastersByProcedureName(String name) {
        return userRepository.findAllMastersByProcedureName(name);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAllByDate(Date date) {
        return userRepository.findAllByDate(date);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllClients() {
        return userRepository.findAllByRole(Role.CLIENT);
    }

    @Override
    public Integer getMasterRatingById(Long id) {
        return userRepository.findRatingByMasterId(id);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
