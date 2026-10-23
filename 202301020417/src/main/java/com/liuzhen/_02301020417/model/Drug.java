package com.liuzhen._02301020417.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Drug {
    private Integer drugId;              // 药品唯一ID
    private String drugName;             // 药品名称
    private String drugType;             // 药品分类
    private String indication;           // 适应症
    private String applicableDepartment; // 适用科室（多个用逗号分隔）
    private String dosageForm;           // 剂型
    private String specification;        // 规格
    private String manufacturer;         // 生产厂家
    private BigDecimal unitPrice;        // 单价（元）
    private Integer stockQuantity;       // 库存数量
    private Integer status;              // 状态（1: 在售 / 0: 下架）
}