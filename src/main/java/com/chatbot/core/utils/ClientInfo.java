package com.chatbot.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class ClientInfo {
    public static String getIPv6Address(HttpServletRequest request) {
        String ipv6 = request.getHeader("X-Forwarded-For");
        log.info("GetIPv6Address X-Forwarded-For {}", ipv6);
        if (StringUtils.hasText(ipv6)) {
            return ipv6.split(",")[0];
        }
        if (ipv6 == null || ipv6.isEmpty() || "unknown".equalsIgnoreCase(ipv6)) {
            ipv6 = request.getRemoteAddr();
        }
        log.info("GetIPv6Address ipv6 {}", ipv6);
        return ipv6;
    }
}
