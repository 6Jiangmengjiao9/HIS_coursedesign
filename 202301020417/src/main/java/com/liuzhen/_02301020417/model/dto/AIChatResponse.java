package com.liuzhen._02301020417.model.dto;

import lombok.Data;

import java.util.List;

/**
 * AI对话响应DTO
 */
@Data
public class AIChatResponse {
    private String id;
    private List<Choice> choices;
    private Usage usage;
    private Long created;
    private String model;
    private String object;

    @Data
    public static class Choice {
        private Message message;
        private String finishReason;

        @Data
        public static class Message {
            private String role;
            private String content;
            private String reasoningContent;
            private List<ToolCall> toolCalls;

            @Data
            public static class ToolCall {
                private String id;
                private String type;
                private Function function;

                @Data
                public static class Function {
                    private String name;
                    private String arguments;
                }
            }
        }
    }

    @Data
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}

