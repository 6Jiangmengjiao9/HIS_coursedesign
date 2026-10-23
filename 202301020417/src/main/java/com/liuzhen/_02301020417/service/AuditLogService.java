package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.AuditLog;
import com.liuzhen._02301020417.security.LoginUser;

import java.util.List;

public interface AuditLogService {
    void record(LoginUser operator, String action, String detail);

    List<AuditLog> getLogs(Integer pageNum, Integer pageSize);

    int count();
}
