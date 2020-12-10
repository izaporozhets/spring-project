package com.springproject.beautysaloon.validator;

import com.springproject.beautysaloon.dto.MasterDto;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class MasterValidator implements Validator {
    private final UserService userService;

    public MasterValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return MasterDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MasterDto master = (MasterDto)o;
        if(userService.findByEmail(master.getEmail()).isPresent()){
            errors.rejectValue("email","","This email is already in use");
        }
        if(master.getSpecialityList().isEmpty()){
            errors.rejectValue("specialityList","","Select speciality");
        }
        if(master.getPassword().equals(master.getConfirmPassword())){
            errors.rejectValue("password","","Passwords dont match");
            errors.rejectValue("confirmPassword","","Passwords dont match");
        }
    }
}
