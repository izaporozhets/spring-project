package com.springproject.beautysaloon.security;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class JwtResponse implements Serializable
{
    private final String username;
    private final String jwttoken;

    public JwtResponse(String username, String jwttoken) {
        this.username = username;
        this.jwttoken = jwttoken;
    }

}