package com.liuzhen._02301020417.model;


import lombok.Data;

import java.util.Date;

@Data
public class Patient {
    private Integer patientId;
    private String name;
    private String gender;
    private Integer age;
    private String address;
    private String idCard;
    private String contact;
    private Date createTime;
    private Integer doctorId;
    private Case caseInfo;
    private Doctor doctor;
}
