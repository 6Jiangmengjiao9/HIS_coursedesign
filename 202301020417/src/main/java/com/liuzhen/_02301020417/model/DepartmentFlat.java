package com.liuzhen._02301020417.model;

import lombok.Data;

@Data
public class DepartmentFlat {
    // 科室信息
    private String departmentId;       // 科室ID
    private String departmentName;     // 科室名称
    private String introduction;       // 科室简介
    private String location;           // 科室位置
    private String phone;              // 科室电话

    // 单个医生信息（不再是集合）
    private Integer doctorId;          // 医生ID
    private String doctorName;         // 医生姓名
    private String title;              // 医生职称
    private String userId;      // 医生用户ID集合（逗号分隔，如"USER1001,USER1002"）
}