package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.User;
import com.liuzhen._02301020417.model.UserInfo;
import com.liuzhen._02301020417.security.JwtService;
import com.liuzhen._02301020417.service.UserService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/getAllUser")
    public Result getAllUser() {
        List<User> result = userService.getAllUser();
        return Result.ok().put("data", result);
    }

    @RequestMapping({"/login", "/api/auth/login"})
    public Result login(String username, String password) {
        User result = userService.login(username, password);
        if (result != null) {
            String token = jwtService.generateToken(result);
            return Result.ok().put("data", result).put("token", token);
        }
        return Result.error(401, "用户名或密码错误");
    }

    @RequestMapping("/register")
    public Result register(String username, String password, String email, String phone, String role,
                           String relname, int gender) {
        UserInfo userInfo = new UserInfo();
        userInfo.setRealname(relname);
        userInfo.setGender(gender);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("patient");
        user.setStatus(1);
        user.setUserInfo(userInfo);

        boolean result = userService.register(user);
        if (result) {
            return Result.ok("注册成功");
        }
        return Result.error("注册失败");
    }
}
