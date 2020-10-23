package com.springproject.beautysaloon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @PostMapping("/success")
    public String getSuccessPage(){
        return "success";
    }
}
