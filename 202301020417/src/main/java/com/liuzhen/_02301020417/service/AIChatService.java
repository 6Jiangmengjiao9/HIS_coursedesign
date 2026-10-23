package com.liuzhen._02301020417.service;

import com.liuzhen._02301020417.model.dto.AIChatRequest;
import com.liuzhen._02301020417.model.dto.AIChatResponse;
import org.springframework.stereotype.Service;

@Service
public interface AIChatService {
    /**
     * 发送消息给AI并获取回复
     * @param userMessage 用户消息
     * @param patientId 患者ID（可选，用于上下文）
     * @return AI回复
     */
    AIChatResponse chat(String userMessage, Integer patientId);
}

