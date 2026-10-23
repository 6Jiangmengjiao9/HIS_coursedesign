package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.Department;
import com.liuzhen._02301020417.model.DepartmentFlat;
import com.liuzhen._02301020417.model.Doctor;
import com.liuzhen._02301020417.service.DepartmentService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;
    @RequestMapping("/getAllDepartment")
    public String departmentList(){
        List<DepartmentFlat> result = departmentService.getDepartmentDoctorsFlat();
        return result.toString();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/updateDepartment")
    public Result updateDepartment(@RequestBody Department  department){
        int flag = departmentService.updateDepartment(department);
        if(flag>0){
            return Result.ok().put("msg","更新成功");
        }else{
            return Result.error().put("msg","更新失败");
        }

    }
}
