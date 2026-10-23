package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.mapper.AuditLogMapper;
import com.liuzhen._02301020417.model.AuditLog;
import com.liuzhen._02301020417.security.LoginUser;
import com.liuzhen._02301020417.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

    public AuditLogServiceImpl(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    @Transactional
    public void record(LoginUser operator, String action, String detail) {
        AuditLog log = new AuditLog();
        log.setOperatorId(operator.getId());
        log.setOperatorName(operator.getUsername());
        log.setRole(operator.getRole());
        log.setAction(action);
        log.setDetail(detail);
        log.setCreatedTime(new Date());
        auditLogMapper.addAuditLog(log);
    }

    @Override
    public List<AuditLog> getLogs(Integer pageNum, Integer pageSize) {
        int page = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return auditLogMapper.getAuditLogs((page - 1) * size, size);
    }

    @Override
    public int count() {
        return auditLogMapper.countAuditLogs();
    }
}
