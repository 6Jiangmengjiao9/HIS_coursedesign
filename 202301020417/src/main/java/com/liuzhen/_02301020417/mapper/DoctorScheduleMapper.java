package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.DoctorSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DoctorScheduleMapper {

    // 查询所有排班
    List<DoctorSchedule> getAllSchedule();

    // 根据排班ID查询
    DoctorSchedule getScheduleById(Integer scheduleId);

    // 根据医生ID查询该医生所有排班
    List<DoctorSchedule> getScheduleByDoctorId(Integer doctorId);

    // 新增排班
    int addSchedule(DoctorSchedule schedule);

    // 更新排班
    int updateSchedule(DoctorSchedule schedule);

    // 删除排班
    int deleteSchedule(Integer scheduleId);

    DoctorSchedule getScheduleForUpdate(Integer scheduleId);

    DoctorSchedule findAvailableScheduleForUpdate(Integer doctorId);

    DoctorSchedule findAvailableScheduleForUpdateByDate(@Param("doctorId") Integer doctorId,
                                                        @Param("scheduleDate") LocalDate scheduleDate);

    int decreaseSlots(Integer scheduleId);
}


