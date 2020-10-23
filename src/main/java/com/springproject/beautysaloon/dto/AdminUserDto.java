package com.springproject.beautysaloon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.Status;
import com.springproject.beautysaloon.model.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUserDto {

    private Long id;
    private int rating;
    private String speciality;
    private int visits;
    private String email;
    private String name;
    private Role role;
    private Status status;

    public User toUser(){
        User user = new User();
        user.setId(id);
        user.setRating(rating);
        user.setVisits(visits);
        user.setEmail(email);
        user.setName(name);
        user.setStatus(status);
        user.setRole(role);
        return user;
    }

    public static AdminUserDto fromUser(User user){
        AdminUserDto adminUserDto = new AdminUserDto();
        adminUserDto.setId(user.getId());
        adminUserDto.setVisits(user.getVisits());
        adminUserDto.setEmail(user.getEmail());
        adminUserDto.setName(user.getName());
        adminUserDto.setStatus(user.getStatus());
        adminUserDto.setRole(user.getRole());
        return adminUserDto;
    }
}
