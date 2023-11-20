package com.chatbot.core.security;

import com.chatbot.core.dto.AuthorizedUserDTO;
import com.chatbot.core.exception.JwtAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.Collection;

public class Utils {

    public static AuthorizedUserDTO getAuthorizedUserOrNull(Principal principal) {
        return principal == null ? null : (AuthorizedUserDTO) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }

    public static AuthorizedUserDTO getAuthorizedUserOrThrow(Principal principal) {
        if (principal == null) {
            throw new JwtAuthenticationException("Authorised failed");
        }
        return (AuthorizedUserDTO) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }

    public static Collection<? extends GrantedAuthority> convertToGrandAuthorities(Collection<String> roles) {
        return roles.stream().map(each -> new SimpleGrantedAuthority("ROLE_" + each)).toList();
    }
}
