package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.mapper.DoctorMapper;
import com.liuzhen._02301020417.model.Doctor;
import com.liuzhen._02301020417.model.Patient;
import com.liuzhen._02301020417.model.User;
import com.liuzhen._02301020417.model.UserInfo;
import com.liuzhen._02301020417.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {
    @Autowired
    DoctorMapper doctorMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<Doctor> getAllDoctor() {
        return doctorMapper.getAllDoctor();
    }

    @Override
    public List<Doctor> getDoctorPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        int offset = (pageNum - 1) * pageSize;
        return doctorMapper.getDoctorPage(offset, pageSize);
    }

    @Override
    public int getDoctorCount() {
        return doctorMapper.getDoctorCount();
    }

    @Override
    @Transactional
    public int addDoctor(Doctor doctor) {
        // 校验 name 字段
        if (doctor.getName() == null || doctor.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("医生姓名不能为空，当前值: " + doctor.getName());
        }

        // 如果包含用户信息，先处理User和UserInfo
        if (doctor.getUser() != null) {
            User user = doctor.getUser();
            // 强制设置role为"doctor"
            user.setRole("doctor");
            // 如果status未设置或为0，默认为1（启用）
            if (user.getStatus() == 0) {
                user.setStatus(1);
            }
            if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            // 先插入User，获取自增id
            int userResult = doctorMapper.addUser(user);
            if (userResult <= 0) {
                throw new RuntimeException("添加用户失败");
            }

            // 如果包含UserInfo，插入UserInfo
            if (doctor.getUserInfo() != null) {
                UserInfo userInfo = doctor.getUserInfo();
                // 设置userid为刚插入的user.id
                userInfo.setUserid(user.getId());
                // 插入UserInfo，获取自增id
                int userInfoResult = doctorMapper.addUserInfo(userInfo);
                if (userInfoResult <= 0) {
                    throw new RuntimeException("添加用户信息失败");
                }
                // 将userInfo设置回doctor对象，以便Mapper中使用userInfo.id
                doctor.setUserInfo(userInfo);
            }
        }

        // 插入Doctor
        return doctorMapper.addDoctor(doctor);
    }

    @Override
    public int deleteDoctor(Integer doctorId) {
        return doctorMapper.deleteDoctor(doctorId);
    }

    @Override
    @Transactional
    public int updateDoctor(Doctor doctor) {
        // 如果包含用户信息，更新User和UserInfo
        if (doctor.getUser() != null && doctor.getUser().getId() > 0) {
            User user = doctor.getUser();
            // 强制设置role为"doctor"，不允许修改
            user.setRole("doctor");
            if (user.getPassword() != null && !user.getPassword().isBlank()
                    && !user.getPassword().startsWith("$2")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            // 更新User
            int userResult = doctorMapper.updateUser(user);
            if (userResult <= 0) {
                throw new RuntimeException("更新用户失败");
            }

            // 如果包含UserInfo，更新UserInfo
            if (doctor.getUserInfo() != null && doctor.getUserInfo().getId() > 0) {
                UserInfo userInfo = doctor.getUserInfo();
                // 确保userid正确
                userInfo.setUserid(user.getId());
                // 更新UserInfo
                int userInfoResult = doctorMapper.updateUserInfo(userInfo);
                if (userInfoResult <= 0) {
                    throw new RuntimeException("更新用户信息失败");
                }
            }
        }

        // 更新Doctor
        return doctorMapper.updateDoctor(doctor);
    }

}
