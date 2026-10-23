package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.Department;
import com.liuzhen._02301020417.model.DepartmentFlat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<DepartmentFlat> getDepartmentDoctorsFlat();
    public int updateDepartment(Department department);
}
