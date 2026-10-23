package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.User;
import com.liuzhen._02301020417.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    // 查询所有用户
    public List<User> getAllUser();
    
    // 根据用户名查询用户
    public User getUserByUserName(String username);
    
    // 根据用户ID查询用户
    public User getUserById(Integer id);
    
    // 新增用户
    public int addUser(User  user);
    
    // 更新用户
    public int updateUser(User user);
    
    // 删除用户
    public int deleteUser(Integer id);
    
    // 用户信息相关操作
    // 根据ID查询用户信息
    public UserInfo getUserInfoById(Integer id);
    
    // 根据用户ID查询用户信息
    public UserInfo getUserInfoByUserId(Integer userId);
    
    // 新增用户信息
    public int addUserInfo(UserInfo userInfo);
    
    // 更新用户信息
    public int updateUserInfo(UserInfo userInfo);
    
    // 删除用户信息
    public int deleteUserInfo(Integer id);
}
