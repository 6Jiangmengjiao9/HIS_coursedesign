package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.Case;
import com.liuzhen._02301020417.model.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PatientMapper {
    public List<Patient> getAllPatient();

    // 分页查询患者列表
    public List<Patient> getPatientPage(Integer offset, Integer pageSize);

    // 患者总数（用于分页）
    public int getPatientCount();

    public Patient getPatientById(Integer patientId);

    public int updatePatientDoctor(@Param("patientId") Integer patientId,
                                   @Param("doctorId") Integer doctorId);

    public int addPatient(Patient patient);
    public int addCaseInfo(Case caseInfo);
    // 根据患者ID查询该患者所有病历
    public java.util.List<Case> getCasesByPatientId(Integer patientId);
    public int deletePatient(Integer  patientId);
    public int deleteCase(Integer patientId);
    public int updatePatient(Patient patient);
    public int updateCase(Case caseInfo);
    public int  addMedicalRecordByPatientId(Case caseInfo);
    public int  updateMedicalRecordByPatientId(Case caseInfo);
}
