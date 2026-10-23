package com.liuzhen._02301020417.model;


import com.liuzhen._02301020417.model.Doctor;
import lombok.Data;
import java.util.List;  // 导入List

@Data
public class Department {
    private String departmentId;
    private String departmentName;
    private String introduction;
    private String location;
    private String phone;
}