package com.springproject.beautysaloon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/client-home")
    public String getClientHomePage(){
        return "client-home";
    }

    @GetMapping("/master-home")
    public String getMasterHomePage(){
        return "master-home";
    }

    @GetMapping("/admin-home")
    public String getAdminHomePage(){
        return "admin-home";
    }
}
