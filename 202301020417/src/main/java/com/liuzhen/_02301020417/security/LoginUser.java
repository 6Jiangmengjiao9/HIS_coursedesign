package com.liuzhen._02301020417.security;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUser implements Serializable {
    private Integer id;
    private String username;
    private String role;

    public LoginUser() {
    }

    public LoginUser(Integer id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
