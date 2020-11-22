package com.springproject.beautysaloon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.Speciality;
import com.springproject.beautysaloon.model.Status;
import com.springproject.beautysaloon.model.User;
import java.util.List;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterDto {

    private Long id;
    private String name;
    private int rating;
    private List<String> specialityList;
    private String email;
    private String password;
    private Role role;
    private Status status;


    public User toUser(){
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setRating(rating);
        user.setEmail(email);
        user.setPassword(password);
        user.setStatus(status);
        user.setRole(role);
        return user;
    }

    public static MasterDto fromUser(User user){
        MasterDto master = new MasterDto();
        master.setId(user.getId());
        master.setName(user.getName());
        master.setRating(user.getRating());
        //master.setSpecialityList(user.getSpecialityList());
        master.setEmail(user.getEmail());
        master.setPassword(user.getPassword());
        master.setStatus(user.getStatus());
        master.setRole(user.getRole());
        return master;
    }


}
