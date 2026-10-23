package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.mapper.DoctorScheduleMapper;
import com.liuzhen._02301020417.model.DoctorSchedule;
import com.liuzhen._02301020417.service.DoctorScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    @Autowired
    DoctorScheduleMapper doctorScheduleMapper;

    @Override
    public List<DoctorSchedule> getAllSchedule() {
        return doctorScheduleMapper.getAllSchedule();
    }

    @Override
    public DoctorSchedule getScheduleById(Integer scheduleId) {
        return doctorScheduleMapper.getScheduleById(scheduleId);
    }

    @Override
    public List<DoctorSchedule> getScheduleByDoctorId(Integer doctorId) {
        return doctorScheduleMapper.getScheduleByDoctorId(doctorId);
    }

    @Transactional
    @Override
    public int addSchedule(DoctorSchedule schedule) {
        // 默认状态为有效
        if (schedule.getStatus() == null) {
            schedule.setStatus(1);
        }
        if (schedule.getRemainingSlots() == null) {
            schedule.setRemainingSlots(schedule.getMaxPatients());
        }
        return doctorScheduleMapper.addSchedule(schedule);
    }

    @Transactional
    @Override
    public int updateSchedule(DoctorSchedule schedule) {
        return doctorScheduleMapper.updateSchedule(schedule);
    }

    @Transactional
    @Override
    public int deleteSchedule(Integer scheduleId) {
        return doctorScheduleMapper.deleteSchedule(scheduleId);
    }
}


