package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.Prescription;
import com.liuzhen._02301020417.security.LoginUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PrescriptionService {
    // 查询所有处方
    List<Prescription> getAllPrescription();

    // 分页查询处方列表
    List<Prescription> getPrescriptionPage(Integer pageNum, Integer pageSize);

    // 获取处方总数（用于分页）
    int getPrescriptionCount();
    
    // 根据处方ID查询
    Prescription getPrescriptionById(Integer prescriptionId);
    
    // 根据患者ID查询处方列表
    List<Prescription> getPrescriptionByPatientId(Integer patientId);
    
    // 根据医生ID查询处方列表
    List<Prescription> getPrescriptionByDoctorId(Integer doctorId);
    
    // 新增处方
    boolean addPrescription(Prescription prescription);
    
    // 更新处方
    boolean updatePrescription(Prescription prescription);
    
    // 删除处方
    boolean deletePrescription(Integer prescriptionId);

    Prescription createPrescription(Prescription request, LoginUser operator);

    Prescription getDetail(Integer prescriptionId);

    List<Prescription> getByStatus(Integer status);
}

