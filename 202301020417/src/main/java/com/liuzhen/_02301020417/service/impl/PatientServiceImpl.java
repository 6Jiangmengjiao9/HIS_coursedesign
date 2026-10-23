package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.mapper.PatientMapper;
import com.liuzhen._02301020417.model.Case;
import com.liuzhen._02301020417.model.Patient;
import com.liuzhen._02301020417.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    PatientMapper patientMapper;
    public List<Patient> getAllPatient() {
        return patientMapper.getAllPatient();
    }

    @Override
    public List<Patient> getPatientPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        int offset = (pageNum - 1) * pageSize;
        return patientMapper.getPatientPage(offset, pageSize);
    }

    @Override
    public int getPatientCount() {
        return patientMapper.getPatientCount();
    }
    @Override
    public List<Case> getCasesByPatientId(Integer patientId) {
        return patientMapper.getCasesByPatientId(patientId);
    }
    @Transactional
    @Override
    public boolean register(Patient patient) {
        // 总是设置创建时间，确保数据一致性
        patient.setCreateTime(new Date());

        int result = patientMapper.addPatient(patient);
        Case caseInfo = patient.getCaseInfo();
        if (caseInfo != null) {
            caseInfo.setPatientId(patient.getPatientId());
            if (result > 0) {
                return patientMapper.addCaseInfo(caseInfo) > 0;
            }
        }
        return result > 0;
    }
    @Transactional
    @Override
    public boolean deletePatientAndCase(Integer patientId) {
        // 删除患者信息
        int patientResult = patientMapper.deletePatient(patientId);

        if (patientResult > 0) {
            // 患者删除成功后，删除病历记录（无论是否有病历记录都算成功）
            patientMapper.deleteCase(patientId);
            return true;
        }
        return false;
    }
    @Transactional
    @Override
    public boolean updatePatientAndCase(Patient patient) {
        // 更新患者信息
        int patientResult = patientMapper.updatePatient(patient);

        Case caseInfo = patient.getCaseInfo();
        if (caseInfo != null) {
            // 使用 patientId 作为 recordId
            caseInfo.setRecordId(patient.getPatientId());
            caseInfo.setPatientId(patient.getPatientId());

            int caseResult = patientMapper.updateCase(caseInfo);
            return patientResult > 0 && caseResult > 0;
        }

        return patientResult > 0;
    }

    @Override
    public int addMedicalRecordByPatientId(Case caseInfo) {
        if (caseInfo.getEntryTime() == null) {
            caseInfo.setEntryTime(new Date());
        }
        return patientMapper.addMedicalRecordByPatientId(caseInfo);
    }

    @Override
    public int updateMedicalRecordByPatientId(Case caseInfo) {
        if (caseInfo.getEntryTime() == null) {
            caseInfo.setEntryTime(new Date());
        }
        return patientMapper.updateMedicalRecordByPatientId(caseInfo);
    }
}
