package com.chatbot.core.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Roles {

    private Long id;
    private Type type;
    private String description;

    public enum Type {
        ADMIN, ARTIST, FOLLOWER, EXPERT, JUDGE_HALL_OF_FAME
    }
}
