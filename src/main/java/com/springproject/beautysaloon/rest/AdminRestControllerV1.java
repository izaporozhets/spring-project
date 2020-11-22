package com.springproject.beautysaloon.rest;

import com.springproject.beautysaloon.repository.UserRepository;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AdminRestControllerV1 {
    private final UserService userService;

    public AdminRestControllerV1(UserService userService) {
        this.userService = userService;
    }




}
