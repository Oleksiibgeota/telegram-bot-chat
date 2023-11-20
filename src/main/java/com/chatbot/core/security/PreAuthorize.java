package com.chatbot.core.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreAuthorize {

    PreAuthorizeStrategy strategy() default PreAuthorizeStrategy.SECURE;

    String keyPath() default "";

    public enum PreAuthorizeStrategy{
        SECURE, CAN_ANONYMOUS_AUTHORIZED_FIRST
    }
}
