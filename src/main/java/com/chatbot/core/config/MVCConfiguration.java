package com.chatbot.core.config;

import com.chatbot.core.security.SecurityResolver;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import java.util.List;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class MVCConfiguration implements WebApplicationInitializer, WebMvcConfigurer {

    private final SecurityResolver securityResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(securityResolver);
    }

    @Override
    public void onStartup(@NotNull ServletContext servletContext) {
    }
}
