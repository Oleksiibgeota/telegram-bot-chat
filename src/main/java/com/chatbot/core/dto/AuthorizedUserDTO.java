package com.chatbot.core.dto;

import com.chatbot.core.dto.wrapper.UIResponseBody;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthorizedUserDTO implements UIResponseBody {

    private Long id;
    private String uuid;
    private String email;
    private Set<String> authorities;
    private String accessToken;
    private String refreshToken;

    public AuthorizedUserDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AuthorizedUserDTO(Long id, String uuid, String email, Set<String> authorities) {
        this.id = id;
        this.uuid = uuid;
        this.email = email;
        this.authorities = authorities;
    }


    public enum UserAccessType {
        ANONYMOUS
    }
}
