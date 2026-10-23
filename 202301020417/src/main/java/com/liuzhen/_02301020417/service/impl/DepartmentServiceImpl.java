package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.mapper.DepartmentMapper;
import com.liuzhen._02301020417.model.Department;
import com.liuzhen._02301020417.model.DepartmentFlat;
import com.liuzhen._02301020417.service.DepartmentService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    DepartmentMapper departmentMapper;
    @Override
    public List<DepartmentFlat> getDepartmentDoctorsFlat() {
        return departmentMapper.getDepartmentDoctorsFlat();
    }

    @Override
    public int updateDepartment(Department department) {
        return departmentMapper.updateDepartment(department);
    }

}
