package com.liuzhen._02301020417.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    private  int id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private String phone;
    private int status;
    private String role;

    private Date create_Time;
    private Date update_Time;

    private UserInfo userInfo;
    private Doctor doctor;

}
