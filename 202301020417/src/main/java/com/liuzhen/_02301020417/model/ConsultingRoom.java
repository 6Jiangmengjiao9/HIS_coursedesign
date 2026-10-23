package com.liuzhen._02301020417.model;

import lombok.Data;

/**
 * 诊室表实体，对应表 consulting_room
 */
@Data
public class ConsultingRoom {
    private Integer roomId;        // 诊室唯一ID
    private String departmentId;   // 关联科室ID
    private String roomNumber;     // 诊室编号（唯一）
    private String roomType;       // 诊室类型
    private Integer capacity;      // 最大容纳人数
    private String location;       // 诊室位置
    private Integer status;        // 诊室状态（1: 可用 / 0: 维护中）
    private String remark;         // 备注
}


