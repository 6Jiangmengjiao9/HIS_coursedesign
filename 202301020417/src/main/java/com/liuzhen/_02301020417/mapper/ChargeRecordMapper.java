package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.ChargeRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChargeRecordMapper {
    int addChargeRecord(ChargeRecord chargeRecord);

    ChargeRecord getByPrescriptionId(Integer prescriptionId);
}
