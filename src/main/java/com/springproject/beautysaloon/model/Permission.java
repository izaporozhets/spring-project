package com.springproject.beautysaloon.model;

public enum Permission {
    CLIENT_READ("client:read"),
    DEVELOPERS_READ("developers:read"),
    DEVELOPERS_WRITE("developers:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission(){
        return permission;
    }
}
