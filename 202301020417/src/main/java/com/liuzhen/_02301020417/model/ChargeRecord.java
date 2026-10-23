package com.liuzhen._02301020417.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ChargeRecord {
    private Integer chargeId;
    private Integer prescriptionId;
    private Date chargeTime;
    private BigDecimal amount;
    private Integer cashierId;
}
