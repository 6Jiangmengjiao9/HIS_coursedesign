package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.Doctor;
import com.liuzhen._02301020417.model.Patient;
import com.liuzhen._02301020417.service.DoctorService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class DoctorController {
    @Autowired
    DoctorService doctorService;
    @RequestMapping("/getAllDoctor")
    public Result doctorList(){
        List<Doctor> result = doctorService.getAllDoctor();
        return Result.ok().put("data",result);
    }

    /**
     * 医生信息分页查询
     * @param pageNum  当前页码（从1开始）
     * @param pageSize 每页数量
     */
    @RequestMapping("/getDoctorPage")
    public Result getDoctorPage(Integer pageNum, Integer pageSize){
        List<Doctor> list = doctorService.getDoctorPage(pageNum, pageSize);
        int total = doctorService.getDoctorCount();
        return Result.ok()
                .put("data", list)
                .put("total", total)
                .put("pageNum", pageNum == null ? 1 : pageNum)
                .put("pageSize", pageSize == null ? 10 : pageSize);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addDoctor")
    public Result addDoctor(@RequestBody Doctor doctor){
        int flag = doctorService.addDoctor(doctor);
        if(flag>0){
            return Result.ok().put("msg","添加成功");
        }else{
            return Result.error().put("msg","添加失败");
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/deleteDoctor")
    public Result deleteDoctor(Integer doctorId){
        int flag = doctorService.deleteDoctor(doctorId);
        if(flag>0){
            return Result.ok().put("msg","删除成功");
        }else{
            return Result.error().put("msg","删除失败");
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/updateDoctor")
    public Result updateDoctor(@RequestBody Doctor doctor){
        int flag = doctorService.updateDoctor(doctor);
        if(flag>0){
            return Result.ok().put("msg","更新成功");
        }else{
            return Result.error().put("msg","更新失败");
        }
    }
}
