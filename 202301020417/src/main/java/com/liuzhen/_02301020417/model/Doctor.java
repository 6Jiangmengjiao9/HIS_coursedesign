package com.liuzhen._02301020417.model;

import lombok.Data;

@Data
public class Doctor {
    private Integer doctorId;
    private String userId;
    private String name;
    private String departmentId;
    private String title;
    private Integer userinfoId;
    private Department department;

    private User user;
    private UserInfo userInfo;
}
