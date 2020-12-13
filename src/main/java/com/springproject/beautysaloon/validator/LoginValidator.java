package com.springproject.beautysaloon.validator;

import com.springproject.beautysaloon.dto.LoginDto;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class LoginValidator implements Validator {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public LoginValidator(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return LoginDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        LoginDto loginDto = (LoginDto)o;
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();
        Optional<User> user = userService.findByEmail(email);

        if(!user.isPresent() && !errors.hasFieldErrors("email")){
            errors.rejectValue("email", "", "No user with this email");
        }
        if(user.isPresent() && !passwordEncoder.matches(password, user.get().getPassword()) && !errors.hasFieldErrors("password")){
            errors.rejectValue("password", "", "Wrong password");
        }

    }
}
