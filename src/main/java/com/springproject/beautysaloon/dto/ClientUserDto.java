package com.springproject.beautysaloon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springproject.beautysaloon.model.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientUserDto {

    private Long id;
    private int visits;
    private String email;
    private String name;

    public User toClient(){
        User user = new User();
        user.setId(id);
        user.setVisits(visits);
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    public static ClientUserDto fromClient(User user){
        ClientUserDto clientUserDto = new ClientUserDto();
        clientUserDto.setId(user.getId());
        clientUserDto.setVisits(user.getVisits());
        clientUserDto.setEmail(user.getEmail());
        clientUserDto.setName(user.getName());
        return clientUserDto;
    }
}
