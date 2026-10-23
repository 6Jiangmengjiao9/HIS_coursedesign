package com.liuzhen._02301020417.model;

import lombok.Data;

import java.util.Date;

@Data
public class AuditLog {
    private Integer logId;
    private Integer operatorId;
    private String operatorName;
    private String role;
    private String action;
    private String detail;
    private Date createdTime;
}
