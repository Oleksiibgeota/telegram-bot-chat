package com.chatbot.core.domain;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    private Long id;
    private String uuid;
    private String password;
    private String email;
    private boolean isActive;
    private Set<String> authorities;
}
