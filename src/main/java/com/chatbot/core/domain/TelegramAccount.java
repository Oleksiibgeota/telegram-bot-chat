package com.chatbot.core.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TelegramAccount {

    private Long id;
    private Long externalId;
    private String firstName;
    private String lastName;
    private String userName;
    private LocalDateTime createdAt;

}
