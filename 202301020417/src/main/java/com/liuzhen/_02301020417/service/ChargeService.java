package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.ChargeRecord;
import com.liuzhen._02301020417.security.LoginUser;

public interface ChargeService {
    ChargeRecord charge(Integer prescriptionId, LoginUser cashier);
}
