package com.springproject.beautysaloon.rest;

import com.springproject.beautysaloon.dto.ClientDto;
import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.security.JwtResponse;
import com.springproject.beautysaloon.security.JwtTokenProvider;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/api/v1/auth/login", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> authenticate(@RequestParam(name="email") String username, @RequestParam(name = "password") String password, HttpServletResponse response){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userService.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User doesn`t exist"));
            String token = jwtTokenProvider.createToken(username, user.getRole().name());

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

            return new ResponseEntity<>(new JwtResponse(username, token),HttpStatus.OK);
        }catch (AuthenticationException | IOException exception){
            Cookie cookie = new Cookie("Authorization", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
        }
    }


    @RequestMapping("/api/v1/auth/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request,response, null);
    }

    @RequestMapping("/api/v1/auth/register")
    public @ResponseBody ResponseEntity<?> register(@ModelAttribute ClientDto clientDto, HttpServletResponse response){
        User client = clientDto.toUser();
        if(client.getPassword().equals(clientDto.getConfirmPassword())){
            client.setPassword(passwordEncoder.encode(clientDto.getPassword()));
            userService.saveUser(client);
        }else{
            return new ResponseEntity<>("Passwords does not match", HttpStatus.FORBIDDEN);
        }
        return authenticate(client.getEmail(), clientDto.getPassword(), response);
    }
}
