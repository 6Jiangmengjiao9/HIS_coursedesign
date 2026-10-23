package com.liuzhen._02301020417.model;

import lombok.Data;

@Data
public class UserInfo {
    private int  id;
    private int  userid;
    private String realname;
    private int gender;
    private String avatar;

    private User user;
}
