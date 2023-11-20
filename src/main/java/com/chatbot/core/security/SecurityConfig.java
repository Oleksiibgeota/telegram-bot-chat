package com.chatbot.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,  // Enables @PreAuthorize and @PostAuthorize
        securedEnabled = true, // Enables @Secured
        jsr250Enabled = true    // Enables @RolesAllowed (Ensures JSR-250 annotations are enabled)
)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ResolveExceptionEntryPoint resolveExceptionEntryPoint;

    private final String[] OPEN_ENDPOINTS = new String[]{ "/api/v1/login/**", "/api/v1/dialogs"};
//    private final String[] GET_OPEN_ENDPOINTS = new String[]{"/api/v1/profiles/**", "/**/products/**", "/**/comments/**"};
//    private final String[] POST_OPEN_ENDPOINTS = new String[]{"/api/v1/search/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(OPEN_ENDPOINTS).permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/api/v1/**").permitAll()
//                .antMatchers(HttpMethod.GET, GET_OPEN_ENDPOINTS).permitAll()
//                .antMatchers(HttpMethod.POST, POST_OPEN_ENDPOINTS).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(resolveExceptionEntryPoint);
        http.cors();
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
