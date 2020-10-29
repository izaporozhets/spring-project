package com.springproject.beautysaloon.rest;

import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.repository.UserRepository;
import com.springproject.beautysaloon.security.JwtResponse;
import com.springproject.beautysaloon.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final UserRepository  userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> authenticate(@RequestParam(name="email") String username, @RequestParam(name = "password") String password, HttpServletResponse response){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User doesn`t exist"));
            String token = jwtTokenProvider.createToken(username, user.getRole().name());

            Cookie cookie = new Cookie("Authorization", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            if(user.getRole().equals(Role.ADMIN)){
                response.sendRedirect("/auth/admin-home");
            }
            if(user.getRole().equals(Role.CLIENT)){
                response.sendRedirect("/auth/client-home");
            }
            if (user.getRole().equals(Role.MASTER)) {
                response.sendRedirect("/auth/master-home");
            }

            return new ResponseEntity<>(new JwtResponse(username, token),HttpStatus.OK);
        }catch (AuthenticationException | IOException exception){
            return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request,response, null);
    }
}
