package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.Doctor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DoctorService {
    List<Doctor> getAllDoctor();

    // 分页查询医生列表
    List<Doctor> getDoctorPage(Integer pageNum, Integer pageSize);

    // 获取医生总数（用于分页）
    int getDoctorCount();

    int addDoctor(Doctor doctor);
    int deleteDoctor(Integer doctorId);
    int updateDoctor(Doctor doctor);
}
