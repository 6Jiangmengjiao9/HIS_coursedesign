package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.PrescriptionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PrescriptionItemMapper {
    int batchInsert(@Param("items") List<PrescriptionItem> items);

    List<PrescriptionItem> getByPrescriptionId(Integer prescriptionId);

    int deleteByPrescriptionId(Integer prescriptionId);
}
