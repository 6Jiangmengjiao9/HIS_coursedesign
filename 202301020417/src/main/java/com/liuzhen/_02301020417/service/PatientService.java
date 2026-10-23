package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.Case;
import com.liuzhen._02301020417.model.Patient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PatientService {
    List<Patient> getAllPatient();

    // 分页查询患者列表
    List<Patient> getPatientPage(Integer pageNum, Integer pageSize);

    // 获取患者总数（用于分页）
    int getPatientCount();

    // 根据患者ID查询该患者所有病历
    List<Case> getCasesByPatientId(Integer patientId);

    boolean register(Patient patient);
    boolean deletePatientAndCase(Integer patientId);
    boolean updatePatientAndCase(Patient patient);
   int addMedicalRecordByPatientId(Case caseInfo);
   int updateMedicalRecordByPatientId(Case caseInfo);
}
