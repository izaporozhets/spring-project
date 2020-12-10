package com.springproject.beautysaloon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.Speciality;
import com.springproject.beautysaloon.model.Status;
import com.springproject.beautysaloon.model.User;
import java.util.List;
import lombok.Data;

import javax.validation.constraints.*;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterDto {

    private Long id;
    @Size(min = 2, max = 14, message = "Name cannot be less than 2 characters or greater then 14")
    private String name;

    @Min(value = 0, message = "Rating cannot be less then 0")
    private int rating;

    @NotEmpty(message = "Master must have at least one speciality")
    private List<Speciality> specialityList;

    @Email
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Size(min = 4,max = 16, message = "Password must be equal or grater than 4 characters and less than 16")
    private String password;

    @Size(min = 4,max = 16, message = "Password must be equal or grater than 4 characters and less than 16")
    private String confirmPassword;

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
        master.setSpecialityList(user.getSpecialityList());
        master.setEmail(user.getEmail());
        master.setPassword(user.getPassword());
        master.setStatus(user.getStatus());
        master.setRole(user.getRole());
        return master;
    }


}
