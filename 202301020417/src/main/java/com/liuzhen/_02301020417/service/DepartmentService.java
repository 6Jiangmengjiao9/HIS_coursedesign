package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.Department;
import com.liuzhen._02301020417.model.DepartmentFlat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {
    List<DepartmentFlat> getDepartmentDoctorsFlat();
    int updateDepartment(Department department);
}
