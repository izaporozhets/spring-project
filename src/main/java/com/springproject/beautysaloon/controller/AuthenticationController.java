package com.springproject.beautysaloon.controller;

import com.springproject.beautysaloon.dto.LoginDto;
import com.springproject.beautysaloon.dto.UserDto;
import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.Status;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.security.JwtTokenProvider;
import com.springproject.beautysaloon.service.UserService;
import com.springproject.beautysaloon.validator.LoginValidator;
import com.springproject.beautysaloon.validator.UserValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Controller
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final LoginValidator loginValidator;
    private final UserValidator userValidator;
    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, LoginValidator loginValidator, UserValidator userValidator, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.loginValidator = loginValidator;
        this.userValidator = userValidator;
        this.userService = userService;
    }


    @GetMapping("/auth/login")
    public String getLoginPage(Model model){
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @PostMapping(value = "/auth/login")
    public String authenticate(@ModelAttribute(name = "loginDto")@Valid LoginDto loginDto, Errors errors, BindingResult result, HttpServletResponse response){
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            User user = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User doesn`t exist"));
            String token = jwtTokenProvider.createToken(email, user.getRole().name());

            Cookie cookie = new Cookie("Authorization", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            if(user.getRole().equals(Role.ADMIN)){
                response.sendRedirect("/admin-home");
            }
            if(user.getRole().equals(Role.CLIENT)){
                response.sendRedirect("/");
            }
            if (user.getRole().equals(Role.MASTER)) {
                response.sendRedirect("/master-home");
            }

            return "index";
        }catch (AuthenticationException | IOException exception){
            loginValidator.validate(loginDto, result);
            return "login";
        }
    }

    @GetMapping("/auth/register")
    public String getRegisterForm(Model model) {
        UserDto userDto = new UserDto();
        userDto.setVisits(0);
        userDto.setRole(Role.CLIENT);
        userDto.setStatus(Status.ACTIVE);
        model.addAttribute("userDto", userDto);
        return "register";
    }

    @PostMapping("/auth/register")
    public String register(@ModelAttribute(name = "userDto") @Valid UserDto userDto, Errors errors, BindingResult result){
        userValidator.validate(userDto, result);
        if(errors.hasErrors()){
            return "register";
        }
        else{
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userService.saveUser(userDto.toUser());
        }
        return "index";
    }
}
