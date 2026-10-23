package com.liuzhen._02301020417.controller;

import com.liuzhen._02301020417.model.dto.AIChatResponse;
import com.liuzhen._02301020417.service.AIChatService;
import com.liuzhen._02301020417.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class AIChatController {

    @Autowired
    private AIChatService aiChatService;

    /**
     * 患者与AI对话接口
     * @param message 患者发送的消息
     * @param patientId 患者ID（可选，用于提供上下文）
     * @return AI回复
     */
    @PostMapping("/chatWithAI")
    public Result chatWithAI(@RequestParam String message, 
                             @RequestParam(required = false) Integer patientId) {
        try {
            AIChatResponse response = aiChatService.chat(message, patientId);
            
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String aiReply = response.getChoices().get(0).getMessage().getContent();
                return Result.ok()
                        .put("data", aiReply)
                        .put("usage", response.getUsage())
                        .put("model", response.getModel());
            } else {
                return Result.error().put("msg", "AI未返回有效回复");
            }
        } catch (Exception e) {
            return Result.error().put("msg", "AI对话失败: " + e.getMessage());
        }
    }

    /**
     * 患者与AI对话接口（使用JSON请求体）
     * @param requestBody 包含message和patientId的JSON对象
     * @return AI回复
     */
    @PostMapping("/chatWithAIJson")
    public Result chatWithAIJson(@RequestBody ChatRequest requestBody) {
        try {
            AIChatResponse response = aiChatService.chat(requestBody.getMessage(), requestBody.getPatientId());
            
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String aiReply = response.getChoices().get(0).getMessage().getContent();
                return Result.ok()
                        .put("data", aiReply)
                        .put("usage", response.getUsage())
                        .put("model", response.getModel());
            } else {
                return Result.error().put("msg", "AI未返回有效回复");
            }
        } catch (Exception e) {
            return Result.error().put("msg", "AI对话失败: " + e.getMessage());
        }
    }

    /**
     * 内部请求DTO
     */
    public static class ChatRequest {
        private String message;
        private Integer patientId;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getPatientId() {
            return patientId;
        }

        public void setPatientId(Integer patientId) {
            this.patientId = patientId;
        }
    }
}

