package com.springproject.beautysaloon.model;

public enum Permission {
    UNKNOWN_READ("unknown:read"),
    CLIENT_WRITE("client:write"),
    CLIENT_READ("client:read"),
    MASTER_READ("master:read"),
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
