package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuditLogMapper {
    int addAuditLog(AuditLog auditLog);

    List<AuditLog> getAuditLogs(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    int countAuditLogs();
}
