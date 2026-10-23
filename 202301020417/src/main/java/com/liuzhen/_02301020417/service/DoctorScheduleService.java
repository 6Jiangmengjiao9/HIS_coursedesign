package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.DoctorSchedule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DoctorScheduleService {

    // 查询所有排班
    List<DoctorSchedule> getAllSchedule();

    // 根据排班ID查询
    DoctorSchedule getScheduleById(Integer scheduleId);

    // 根据医生ID查询排班
    List<DoctorSchedule> getScheduleByDoctorId(Integer doctorId);

    // 新增排班
    int addSchedule(DoctorSchedule schedule);

    // 更新排班
    int updateSchedule(DoctorSchedule schedule);

    // 删除排班
    int deleteSchedule(Integer scheduleId);
}


