package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.mapper.PatientMapper;
import com.liuzhen._02301020417.mapper.UserMapper;
import com.liuzhen._02301020417.model.Patient;
import com.liuzhen._02301020417.model.User;
import com.liuzhen._02301020417.model.UserInfo;
import com.liuzhen._02301020417.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PatientMapper patientMapper;

    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.getUserByUserName(username);
        if (user == null || user.getPassword() == null) {
            return null;
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        if (user.getPassword().equals(password)) {
            user.setPassword(passwordEncoder.encode(password));
            userMapper.updateUser(user);
            return user;
        }
        return null;
    }
    @Override
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    @Transactional
    @Override
    public boolean register(User user) {
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("patient");
        }
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        int result = userMapper.addUser(user);
        UserInfo userInfo = user.getUserInfo();
        boolean saved = result > 0;
        if (saved && userInfo != null) {
            userInfo.setUserid(user.getId());
            saved = userMapper.addUserInfo(userInfo) > 0;
        }
        if (saved && "patient".equalsIgnoreCase(user.getRole())) {
            ensurePatientRecord(user, userInfo);
        }
        return saved;
    }

    private void ensurePatientRecord(User user, UserInfo userInfo) {
        String realName = userInfo != null && userInfo.getRealname() != null
                ? userInfo.getRealname() : user.getUsername();
        String contact = user.getPhone();
        boolean exists = patientMapper.getAllPatient().stream()
                .anyMatch(item -> Objects.equals(realName, item.getName())
                        && Objects.equals(contact, item.getContact()));
        if (exists) {
            return;
        }
        Patient patient = new Patient();
        patient.setName(realName);
        patient.setGender(userInfo != null && userInfo.getGender() == 2 ? "女" : "男");
        patient.setContact(contact);
        patient.setCreateTime(new Date());
        patientMapper.addPatient(patient);
    }

    @Transactional
    @Override
    public boolean updateUser(User user) {
        // 如果密码为空或空字符串，则查询原密码并设置回去
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            User originalUser = userMapper.getUserById(user.getId());
            if (originalUser != null) {
                user.setPassword(originalUser.getPassword());
            }
        } else if (!user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userMapper.updateUser(user) > 0;
    }

    @Transactional
    @Override
    public boolean deleteUser(Integer id) {
        // 先删除关联的用户信息
        UserInfo userInfo = userMapper.getUserInfoByUserId(id);
        if (userInfo != null) {
            userMapper.deleteUserInfo(userInfo.getId());
        }
        // 再删除用户
        return userMapper.deleteUser(id) > 0;
    }

    @Override
    public UserInfo getUserInfoById(Integer id) {
        return userMapper.getUserInfoById(id);
    }

    @Override
    public UserInfo getUserInfoByUserId(Integer userId) {
        return userMapper.getUserInfoByUserId(userId);
    }

    @Transactional
    @Override
    public boolean addUserInfo(UserInfo userInfo) {
        return userMapper.addUserInfo(userInfo) > 0;
    }

    @Transactional
    @Override
    public boolean updateUserInfo(UserInfo userInfo) {
        return userMapper.updateUserInfo(userInfo) > 0;
    }

    @Transactional
    @Override
    public boolean deleteUserInfo(Integer id) {
        return userMapper.deleteUserInfo(id) > 0;
    }

}
