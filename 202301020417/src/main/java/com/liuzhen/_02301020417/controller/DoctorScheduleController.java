package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.DoctorSchedule;
import com.liuzhen._02301020417.service.DoctorScheduleService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class DoctorScheduleController {

    @Autowired
    DoctorScheduleService doctorScheduleService;

    /**
     * 查询所有排�?
     */
    @RequestMapping("/getAllSchedule")
    public Result getAllSchedule() {
        List<DoctorSchedule> result = doctorScheduleService.getAllSchedule();
        return Result.ok().put("data", result);
    }

    /**
     * 根据排班ID查询
     */
    @RequestMapping("/getScheduleById")
    public Result getScheduleById(Integer scheduleId) {
        DoctorSchedule schedule = doctorScheduleService.getScheduleById(scheduleId);
        if (schedule != null) {
            return Result.ok().put("data", schedule);
        }
        return Result.error().put("msg", "未找到该排班记录");
    }

    /**
     * 根据医生ID查询排班
     */
    @RequestMapping("/getScheduleByDoctorId")
    public Result getScheduleByDoctorId(Integer doctorId) {
        List<DoctorSchedule> result = doctorScheduleService.getScheduleByDoctorId(doctorId);
        return Result.ok().put("data", result);
    }

    /**
     * 新增排班
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addSchedule")
    public Result addSchedule(@RequestBody DoctorSchedule schedule) {
        int flag = doctorScheduleService.addSchedule(schedule);
        if (flag > 0) {
            return Result.ok().put("msg", "添加排班成功");
        }
        return Result.error().put("msg", "添加排班失败");
    }

    /**
     * 更新排班
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateSchedule")
    public Result updateSchedule(@RequestBody DoctorSchedule schedule) {
        int flag = doctorScheduleService.updateSchedule(schedule);
        if (flag > 0) {
            return Result.ok().put("msg", "更新排班成功");
        }
        return Result.error().put("msg", "更新排班失败");
    }

    /**
     * 删除排班
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/deleteSchedule")
    public Result deleteSchedule(Integer scheduleId) {
        int flag = doctorScheduleService.deleteSchedule(scheduleId);
        if (flag > 0) {
            return Result.ok().put("msg", "删除排班成功");
        }
        return Result.error().put("msg", "删除排班失败");
    }
}


