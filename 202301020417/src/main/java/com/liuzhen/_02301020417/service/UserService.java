package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.User;
import com.liuzhen._02301020417.model.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    // 查询所有用户
    List<User> getAllUser();
    
    // 根据用户ID查询用户
    User getUserById(Integer id);
    
    // 登录
    public User login(String username, String password);
    
    // 注册
    public boolean register(User  user);
    
    // 更新用户
    boolean updateUser(User user);
    
    // 删除用户（同时删除关联的用户信息）
    boolean deleteUser(Integer id);
    
    // 用户信息相关操作
    // 根据ID查询用户信息
    UserInfo getUserInfoById(Integer id);
    
    // 根据用户ID查询用户信息
    UserInfo getUserInfoByUserId(Integer userId);
    
    // 新增用户信息
    boolean addUserInfo(UserInfo userInfo);
    
    // 更新用户信息
    boolean updateUserInfo(UserInfo userInfo);
    
    // 删除用户信息
    boolean deleteUserInfo(Integer id);
}
