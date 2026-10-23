package com.liuzhen._02301020417.model;

import lombok.Data;

import java.util.Date;

/**
 * 医生排班表实体，对应表 doctor_schedule
 */
@Data
public class DoctorSchedule {
    private Integer scheduleId;     // 排班记录ID
    private Integer doctorId;       // 医生ID
    private Date scheduleDate;      // 排班日期
    private String shiftType;       // 班次类型（上午/下午/夜班）
    private String startTime;       // 班次开始时间 HH:mm:ss
    private String endTime;         // 班次结束时间 HH:mm:ss
    private String departmentId;    // 出诊科室ID
    private String roomNumber;      // 出诊诊室
    private Integer maxPatients;    // 最大接诊人数
    private Integer remainingSlots; // 剩余号源
    private Integer status;         // 状态（1: 有效 / 0: 取消）
}


