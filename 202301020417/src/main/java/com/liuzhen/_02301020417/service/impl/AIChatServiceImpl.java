package com.liuzhen._02301020417.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuzhen._02301020417.model.dto.AIChatRequest;
import com.liuzhen._02301020417.model.dto.AIChatResponse;
import com.liuzhen._02301020417.service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIChatServiceImpl implements AIChatService {

    private static final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";
    private static final String API_TOKEN = "sk-xemwbxmrezjolxulpfyxbvkhtqojbhyyetcwntcripptqgcg";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public AIChatResponse chat(String userMessage, Integer patientId) {
        try {
            // 构建请求
            AIChatRequest request = new AIChatRequest();
            
            // 构建消息列表
            List<AIChatRequest.Message> messages = new ArrayList<>();
            
            // 如果有患者ID，可以添加系统提示或上下文
            if (patientId != null) {
                AIChatRequest.Message systemMessage = new AIChatRequest.Message();
                systemMessage.setRole("system");
                systemMessage.setContent("你是一位专业的医疗助手，正在为患者ID " + patientId + " 提供咨询服务。请用专业、友好、易懂的方式回答问题。");
                messages.add(systemMessage);
            }
            
            // 添加用户消息
            AIChatRequest.Message userMsg = new AIChatRequest.Message();
            userMsg.setRole("user");
            userMsg.setContent(userMessage);
            messages.add(userMsg);
            
            request.setMessages(messages);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_TOKEN);

            // 构建HTTP请求
            HttpEntity<AIChatRequest> httpEntity = new HttpEntity<>(request, headers);

            // 发送请求
            ResponseEntity<AIChatResponse> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    httpEntity,
                    AIChatResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI对话服务调用失败: " + e.getMessage(), e);
        }
    }
}

