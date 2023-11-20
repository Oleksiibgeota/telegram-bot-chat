package com.chatbot.core.domain;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    private Long id;
    private String sessionUuid;
    private Long userId;
    private String ip;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;


}
