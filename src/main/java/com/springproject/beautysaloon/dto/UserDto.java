package com.springproject.beautysaloon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.Status;
import com.springproject.beautysaloon.model.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private Long id;
    private int visits;

    @NotBlank(message = "Email cannot be empty")
    @Email
    private String email;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Password cannot be empty")
    @Size(min = 4,max = 16, message = "Password must be equal or grater than 4 characters and less than 16")
    private String password;

    @NotNull(message = "Password cannot be empty")
    @Size(min = 4,max = 16, message = "Password must be equal or grater than 4 characters and less than 16")
    private String confirmPassword;

    private Role role;
    private Status status;

    public User toUser(){
        User user = new User();
        user.setId(id);
        user.setVisits(visits);
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }

    public static UserDto fromUser(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setVisits(user.getVisits());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setPassword(user.getPassword());
        userDto.setRole(user.getRole());
        userDto.setStatus(user.getStatus());
        return userDto;
    }
}
