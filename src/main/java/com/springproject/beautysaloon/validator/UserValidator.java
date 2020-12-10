package com.springproject.beautysaloon.validator;

import com.springproject.beautysaloon.dto.UserDto;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UserDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserDto userDto = (UserDto)o;
        if(userService.findByEmail(userDto.getEmail()).isPresent()){
            errors.rejectValue("email","","This email is already in use");
        }
        if(!userDto.getPassword().equals(userDto.getConfirmPassword())){
            errors.rejectValue("password","","Passwords dont match");
            errors.rejectValue("confirmPassword","","Passwords dont match");
        }
    }
}
