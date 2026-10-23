package com.liuzhen._02301020417.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrescriptionItem {
    private Integer itemId;
    private Integer prescriptionId;
    private Integer medicineId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String medicineName;
    private String specification;
    private String dosageForm;
}
