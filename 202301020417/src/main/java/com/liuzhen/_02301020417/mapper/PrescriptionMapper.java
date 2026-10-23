package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.Prescription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface PrescriptionMapper {
    // 查询所有处方
    List<Prescription> getAllPrescription();

    // 分页查询处方列表
    List<Prescription> getPrescriptionPage(Integer offset, Integer pageSize);

    // 处方总数（用于分页）
    int getPrescriptionCount();
    
    // 根据处方ID查询
    Prescription getPrescriptionById(Integer prescriptionId);
    
    // 根据患者ID查询处方列表
    List<Prescription> getPrescriptionByPatientId(Integer patientId);
    
    // 根据医生ID查询处方列表
    List<Prescription> getPrescriptionByDoctorId(Integer doctorId);
    
    // 新增处方
    int addPrescription(Prescription prescription);
    
    // 更新处方
    int updatePrescription(Prescription prescription);
    
    // 删除处方
    int deletePrescription(Integer prescriptionId);

    Prescription getPrescriptionForUpdate(Integer prescriptionId);

    List<Prescription> getPrescriptionByStatus(Integer status);

    int updatePrescriptionStatus(@Param("prescriptionId") Integer prescriptionId,
                                 @Param("status") Integer status);

    int updateTotalFeeAndStatus(@Param("prescriptionId") Integer prescriptionId,
                                @Param("totalFee") BigDecimal totalFee,
                                @Param("status") Integer status);
}

