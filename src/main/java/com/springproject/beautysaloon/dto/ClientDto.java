package com.springproject.beautysaloon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.Status;
import com.springproject.beautysaloon.model.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto {

    private Long id;
    private int visits;
    private String email;
    private String name;
    private String password;
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

    public static ClientDto fromUser(User user){
        ClientDto clientDto = new ClientDto();
        clientDto.setId(user.getId());
        clientDto.setVisits(user.getVisits());
        clientDto.setEmail(user.getEmail());
        clientDto.setName(user.getName());
        clientDto.setPassword(user.getPassword());
        clientDto.setRole(user.getRole());
        clientDto.setStatus(user.getStatus());
        return clientDto;
    }
}
