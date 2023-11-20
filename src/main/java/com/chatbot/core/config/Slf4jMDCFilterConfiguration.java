package com.chatbot.core.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class Slf4jMDCFilterConfiguration implements Filter {

    private final static String CORRELATION_ID_KEY = "correlation-id";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) servletRequest;
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            String cid = req.getHeader(CORRELATION_ID_KEY);
            MDC.put("httpRequestId", generateUniqueCorrelationId());
            log.debug("[cid: {}] REQ {} {}?{}", cid, req.getMethod(), req.getRequestURL(), req.getQueryString());
            resp.addHeader(CORRELATION_ID_KEY, cid);
            chain.doFilter(req, resp);
            log.debug("[cid: {}] RESP status: {}", cid, resp.getStatus());
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    private String generateUniqueCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
