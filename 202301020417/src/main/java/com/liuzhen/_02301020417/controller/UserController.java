package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.User;
import com.liuzhen._02301020417.model.UserInfo;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.UserService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 根据用户ID查询用户
     */
    @RequestMapping("/getUserById")
    public Result getUserById(Integer id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return Result.ok().put("data", user);
        }
        return Result.error().put("msg", "未找到该用户");
    }

    /**
     * 新增用户
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/addUser")
    public Result addUser(@RequestBody User user) {
        boolean flag = userService.register(user);
        if (flag) {
            return Result.ok().put("msg", "添加成功");
        } else {
            return Result.error().put("msg", "添加失败");
        }
    }

    /**
     * 更新用户
     */
    @RequestMapping("/updateUser")
    public Result updateUser(@RequestBody User user, @AuthenticationPrincipal LoginUser current) {
        if (!"admin".equalsIgnoreCase(current.getRole()) && user.getId() != current.getId()) {
            return Result.error(403, "只能修改自己的账号");
        }
        boolean flag = userService.updateUser(user);
        if (flag) {
            return Result.ok().put("msg", "更新成功");
        } else {
            return Result.error().put("msg", "更新失败");
        }
    }

    /**
     * 删除用户
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/deleteUser")
    public Result deleteUser(Integer id) {
        boolean flag = userService.deleteUser(id);
        if (flag) {
            return Result.ok().put("msg", "删除成功");
        } else {
            return Result.error().put("msg", "删除失败");
        }
    }

    /**
     * 用户信息相关接口
     */
    /**
     * 根据ID查询用户信息
     */
    @RequestMapping("/getUserInfoById")
    public Result getUserInfoById(Integer id) {
        UserInfo userInfo = userService.getUserInfoById(id);
        if (userInfo != null) {
            return Result.ok().put("data", userInfo);
        }
        return Result.error().put("msg", "未找到该用户信息");
    }

    /**
     * 根据用户ID查询用户信息
     */
    @RequestMapping("/getUserInfoByUserId")
    public Result getUserInfoByUserId(Integer userId) {
        UserInfo userInfo = userService.getUserInfoByUserId(userId);
        if (userInfo != null) {
            return Result.ok().put("data", userInfo);
        }
        return Result.error().put("msg", "未找到该用户信息");
    }

    /**
     * 新增用户信息
     */
    @RequestMapping("/addUserInfo")
    public Result addUserInfo(@RequestBody UserInfo userInfo, @AuthenticationPrincipal LoginUser current) {
        if (!"admin".equalsIgnoreCase(current.getRole()) && userInfo.getUserid() != current.getId()) {
            return Result.error(403, "只能维护自己的资料");
        }
        boolean flag = userService.addUserInfo(userInfo);
        if (flag) {
            return Result.ok().put("msg", "添加成功");
        } else {
            return Result.error().put("msg", "添加失败");
        }
    }

    /**
     * 更新用户信息
     */
    @RequestMapping("/updateUserInfo")
    public Result updateUserInfo(@RequestBody UserInfo userInfo, @AuthenticationPrincipal LoginUser current) {
        if (!"admin".equalsIgnoreCase(current.getRole()) && userInfo.getUserid() != current.getId()) {
            return Result.error(403, "只能维护自己的资料");
        }
        boolean flag = userService.updateUserInfo(userInfo);
        if (flag) {
            return Result.ok().put("msg", "更新成功");
        } else {
            return Result.error().put("msg", "更新失败");
        }
    }

    /**
     * 删除用户信息
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/deleteUserInfo")
    public Result deleteUserInfo(Integer id) {
        boolean flag = userService.deleteUserInfo(id);
        if (flag) {
            return Result.ok().put("msg", "删除成功");
        } else {
            return Result.error().put("msg", "删除失败");
        }
    }
}

