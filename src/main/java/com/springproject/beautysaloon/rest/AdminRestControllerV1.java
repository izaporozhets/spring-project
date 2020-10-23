package com.springproject.beautysaloon.rest;

import com.springproject.beautysaloon.dto.AdminUserDto;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestControllerV1 {
    private final UserRepository userRepository;

    public AdminRestControllerV1(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @GetMapping(value = "/users")
    @PreAuthorize("hasAuthority('developers:read')")
    public List<AdminUserDto> getAll(){
        List<AdminUserDto> users = new ArrayList<>();
        for(User user : userRepository.findAll()){
            users.add(AdminUserDto.fromUser(user));
        }
        return users;
    }

    @GetMapping(value = "/user/{id}")
    @PreAuthorize("hasAuthority('developers:read')")
    public ResponseEntity<AdminUserDto> getUserById(@PathVariable(name = "id") Long id){
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()){
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        AdminUserDto result = AdminUserDto.fromUser(user.get());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }



}
