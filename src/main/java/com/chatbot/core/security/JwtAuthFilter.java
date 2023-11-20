package com.chatbot.core.security;

import com.chatbot.core.dto.AuthorizedUserDTO;
import com.chatbot.core.dto.wrapper.ErrorDTO;
import com.chatbot.core.dto.wrapper.UIResponse;
import com.chatbot.core.dto.wrapper.UIResponseBody;
import com.chatbot.core.exception.JwtAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwtToken = authHeader.substring(7);
        AuthorizedUserDTO authorizedUser;
        try {
            authorizedUser = jwtTokenProvider.parseAccessToken(jwtToken);
        } catch (Exception e) {

            // ErrorResponse is a public return object that you define yourself
            UIResponse<UIResponseBody> uiResponse = new UIResponse<>();
            uiResponse.setSuccess(false);
            uiResponse.setErrors(List.of(new ErrorDTO("LOGIN_USER", "Unauthorized Access")));

            byte[] responseToSend = restResponseBytes(uiResponse);
            ((HttpServletResponse) response).setHeader("Content-Type", "application/json");
            ((HttpServletResponse) response).setStatus(401);
            response.getOutputStream().write(responseToSend);
            return;
        }
        if (authorizedUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean tokenValid;
            try {
                tokenValid =
                        this.jwtTokenProvider.isTokenValid(jwtToken, authorizedUser);
            } catch (Exception e) {
                throw new JwtAuthenticationException("parse error");
            }
            if (tokenValid) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                authorizedUser,
                                null,
                                Utils.convertToGrandAuthorities(authorizedUser.getAuthorities())
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private byte[] restResponseBytes(UIResponse<UIResponseBody> obj) throws IOException {
        String serialized = new ObjectMapper().writeValueAsString(obj);
        return serialized.getBytes();
    }

}
