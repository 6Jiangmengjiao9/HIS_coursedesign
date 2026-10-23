package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.Prescription;
import com.liuzhen._02301020417.security.LoginUser;

public interface DispenseService {
    Prescription dispense(Integer prescriptionId, LoginUser pharmacist);
}
