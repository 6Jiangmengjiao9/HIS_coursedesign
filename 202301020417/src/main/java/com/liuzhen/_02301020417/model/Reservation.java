package com.liuzhen._02301020417.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;

@Data
public class Reservation {
    private Integer id;
    private String departmentId;
    private Integer doctorId;
    private Integer patientId;          // 与患者表关联
    private Integer scheduleId;         // 排班号源
    private Integer reservationStatus;
    private Integer visitStatus;
    private Integer registerStatus;     // 0 未就诊 1 已开方 2 已完成
    private String patientName;
    private Integer patientAge;
    private String patientPhone;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reservationTime;

    private String departmentName;
    private String doctorName;
}
