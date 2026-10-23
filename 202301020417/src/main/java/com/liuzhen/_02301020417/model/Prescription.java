package com.liuzhen._02301020417.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class Prescription {
    private Integer prescriptionId;      // 处方唯一ID
    private Integer registerId;          // 关联挂号单ID
    private Integer patientId;           // 关联患者ID
    private Integer doctorId;            // 开处方医生ID
    private Date prescriptionDate;       // 处方开具日期
    private String drugInfo;            // 药品信息摘要
    private String diagnosis;            // 诊断结果
    private BigDecimal totalFee;         // 后端计算的处方总费用
    private Integer status;              // 0 待收费 1 已收费 2 已发药
    private List<PrescriptionItem> items;
}

