package com.springproject.beautysaloon.rest;

import com.springproject.beautysaloon.dto.AuthenticationRequestDTO;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.repository.UserRepository;
import com.springproject.beautysaloon.security.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public @ResponseBody ResponseEntity<?> authenticate(@RequestParam(name="username") String username, @RequestParam(name = "password") String password){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User doesn`t exist"));
            String token = jwtTokenProvider.createToken(username, user.getRole().name());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", token);

            Map<Object, Object> result = new HashMap<>();
            result.put("email", username);
            result.put("token", token);

            return new ResponseEntity<>(result,headers,HttpStatus.OK);
        }catch (AuthenticationException exception){
            return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request,response, null);
    }
}
