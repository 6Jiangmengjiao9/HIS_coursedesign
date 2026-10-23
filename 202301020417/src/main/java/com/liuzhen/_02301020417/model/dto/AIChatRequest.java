package com.liuzhen._02301020417.model.dto;

import lombok.Data;

import java.util.List;

/**
 * AI对话请求DTO
 */
@Data
public class AIChatRequest {
    private String model = "Qwen/QwQ-32B";
    private List<Message> messages;
    private Boolean stream = false;
    private Integer maxTokens = 4096;
    private Boolean enableThinking = false;
    private Integer thinkingBudget = 4096;
    private Double minP = 0.05;
    private List<String> stop = null;
    private Double temperature = 0.7;
    private Double topP = 0.7;
    private Integer topK = 50;
    private Double frequencyPenalty = 0.5;
    private Integer n = 1;
    private ResponseFormat responseFormat = new ResponseFormat();

    @Data
    public static class Message {
        private String role;  // "user" or "assistant"
        private String content;
    }

    @Data
    public static class ResponseFormat {
        private String type = "text";
    }
}

