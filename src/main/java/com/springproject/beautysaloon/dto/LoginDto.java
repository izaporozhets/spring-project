package com.springproject.beautysaloon.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginDto {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Wrong email syntax")
    private String email;
    @Size(min = 4,max = 16, message = "Password must be equal or grater than 4 characters and less than 16")
    private String password;

}
