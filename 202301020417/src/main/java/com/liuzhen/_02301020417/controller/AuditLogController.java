package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.AuditLog;
import com.liuzhen._02301020417.service.AuditLogService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PreAuthorize("hasAnyRole('PHARMACIST','ADMIN')")
    @GetMapping({"/api/audit-logs", "/getAuditLogs"})
    public Result getAuditLogs(Integer pageNum, Integer pageSize) {
        List<AuditLog> logs = auditLogService.getLogs(pageNum, pageSize);
        return Result.ok()
                .put("data", logs)
                .put("total", auditLogService.count())
                .put("pageNum", pageNum == null ? 1 : pageNum)
                .put("pageSize", pageSize == null ? 10 : pageSize);
    }
}
