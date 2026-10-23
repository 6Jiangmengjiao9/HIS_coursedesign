package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.Department;
import com.liuzhen._02301020417.model.Doctor;
import com.liuzhen._02301020417.model.User;
import com.liuzhen._02301020417.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DoctorMapper {
    // Doctor表操作
    public List<Doctor> getAllDoctor();

    // 分页查询医生列表
    public List<Doctor> getDoctorPage(Integer offset, Integer pageSize);

    // 医生总数（用于分页）
    public int getDoctorCount();

    public int addDoctor(Doctor doctor);
    public int deleteDoctor(Integer doctorId);
    public int updateDoctor(Doctor doctor);

    Doctor getDoctorById(Integer doctorId);

    Doctor getDoctorByUserId(Integer userId);

    // User表操作
    public List<User> getAllUser();
    public int addUser(User user);
    public int updateUser(User user);
    public int deleteUser(Integer id);

    // UserInfo表操作
    public List<UserInfo> getAllUserInfo();
    public int addUserInfo(UserInfo userInfo);
    public int updateUserInfo(UserInfo userInfo);
    public int deleteUserInfo(Integer id);

}
