package com.chatbot.core.security;


import com.chatbot.core.dto.AuthorizedUserDTO;
import com.chatbot.core.exception.LoginUserException;
import com.chatbot.core.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import static org.apache.tomcat.websocket.Constants.AUTHORIZATION_HEADER_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(AuthorizedUserDTO.class)
                ||
                isOptional(methodParameter);
    }

    private boolean isOptional(MethodParameter methodParameter) {
        Type optType = new ParameterizedTypeReference<Optional<AuthorizedUserDTO>>() {
        }.getType();
        return methodParameter.getGenericParameterType().equals(optType);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        boolean isPreauthorize = Arrays.asList(methodParameter.getMethodAnnotations()).stream().anyMatch(it -> it instanceof PreAuthorize);

        if (isPreauthorize) {
            PreAuthorize parameterAnnotation = methodParameter.getMethodAnnotation(PreAuthorize.class);
            PreAuthorize.PreAuthorizeStrategy strategy = parameterAnnotation.strategy();
            if (PreAuthorize.PreAuthorizeStrategy.CAN_ANONYMOUS_AUTHORIZED_FIRST != strategy) {
                throw new LoginUserException("Login forbidden, please contact support");
            }
            if (StringUtils.isEmpty(nativeWebRequest.getHeader(AUTHORIZATION_HEADER_NAME))) {
                AuthorizedUserDTO authorizedUser = new AuthorizedUserDTO();
                authorizedUser.setUuid(AuthorizedUserDTO.UserAccessType.ANONYMOUS.name());
                return authorizedUser;
            }
        }
        String accessToken;
        try {
            accessToken = getAccessToken(nativeWebRequest);
        } catch (JwtAuthenticationException e) {
            if (isOptional(methodParameter)) {
                return Optional.empty();
            } else {
                throw e;
            }
        }

        AuthorizedUserDTO authorizedUser = jwtTokenProvider.parseAccessToken(accessToken);

        return isOptional(methodParameter) ? Optional.of(authorizedUser) : authorizedUser;
    }

    private String getAccessToken(NativeWebRequest nativeWebRequest) {
        String accessToken = nativeWebRequest.getHeader(AUTHORIZATION_HEADER_NAME);
        if (StringUtils.isNotBlank(accessToken) && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        } else throw new JwtAuthenticationException("Unable to get access token");
    }

}
