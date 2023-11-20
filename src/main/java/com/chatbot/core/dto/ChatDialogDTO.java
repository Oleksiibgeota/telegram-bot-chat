package com.chatbot.core.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatDialogDTO {

    private Long id;
    private Long telegramAccountId;
    private String userRequest;
    private String botResponse;
    private LocalDateTime createdAt;
}
