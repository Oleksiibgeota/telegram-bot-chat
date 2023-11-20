package com.chatbot.core.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatDialog {

    private Long id;
    private Long telegramAccountId;
    private String userRequest;
    private String botResponse;
    private LocalDateTime createdAt;

}
