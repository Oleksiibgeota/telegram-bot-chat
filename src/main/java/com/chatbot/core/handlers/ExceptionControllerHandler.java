package com.chatbot.core.handlers;

import com.chatbot.core.dto.wrapper.ErrorDTO;
import com.chatbot.core.dto.wrapper.UIResponse;
import com.chatbot.core.exception.BaseException;
import com.chatbot.core.exception.JwtAuthenticationException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.base.CaseFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionControllerHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS =
            new HashMap<Class<? extends Exception>, HttpStatus>() {
                {
                    put(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
                    put(IllegalArgumentException.class, HttpStatus.BAD_REQUEST);
                    put(InvalidFormatException.class, HttpStatus.BAD_REQUEST);
                    put(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
                    put(BindException.class, HttpStatus.BAD_REQUEST);
                    put(ConstraintViolationException.class, HttpStatus.BAD_REQUEST);
                    put(JwtAuthenticationException.class, HttpStatus.UNAUTHORIZED);
                    put(InsufficientAuthenticationException.class, HttpStatus.UNAUTHORIZED);
                    put(AccessDeniedException.class, HttpStatus.FORBIDDEN);
                }
            };

    private @Value("${exception.handler.mode:STACKTRACE}")
    HandlerMode handlerMode;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UIResponse<?>> handleAll(Exception e) {
        if (!(e instanceof BaseException)) {
            log.error(e.getClass().getSimpleName() + " original ", e);
            e = convertToBaseException(e);
        }
        return handleBaseException((BaseException) e);
    }

    private BaseException convertToBaseException(Exception e) {
        String exception = classNameToCode(e);
        HttpStatus statusFromMap = EXCEPTION_STATUS.get(e.getClass());
        String httpRequestId = String.format("Request ID={%s} ", MDC.get("httpRequestId"));
        String message;
        if (e.getClass().equals(MethodArgumentNotValidException.class)) {
            message = outputMethodArgumentNotValid(e);
        } else if (e.getClass().equals(HttpMessageNotReadableException.class)) {
            message = e.getMessage();
        } else if (e.getClass().equals(BindException.class)) {
            message = outputBindNotValidException(e);
        } else {
            message = e.getMessage();
        }

        switch (handlerMode) {
            case ERROR_MESSAGE:
                return new BaseException(exception, httpRequestId + e.getMessage(), 500, e);
            case STACKTRACE:
                return new BaseException(exception,
                        httpRequestId + Arrays.toString(e.getStackTrace()), 500, e);
            default:
                return statusFromMap != null
                        ? new BaseException(exception, message, statusFromMap.value(), e)
                        : new BaseException(exception, httpRequestId + "Internal Error", 500, e);
        }
    }

    private String classNameToCode(Exception e) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE,
                e.getClass().getSimpleName().replace("Exception", ""));
    }

    private ResponseEntity<UIResponse<?>> handleBaseException(BaseException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(
                Objects.requireNonNull(HttpStatus.resolve(e.getHttpStatus()))).body(
                new UIResponse<>(Collections.singletonList(
                        new ErrorDTO(e.getId(), e.getMessage()))));
    }

    private String outputMethodArgumentNotValid(Exception e) {
        List<ObjectError> list = ((MethodArgumentNotValidException) e).getBindingResult()
                .getAllErrors();
        return list.stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }

    private String outputBindNotValidException(Exception e) {
        List<ObjectError> list = ((BindException) e).getBindingResult()
                .getAllErrors();
        return list.stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }

    private enum HandlerMode {
        STACKTRACE,
        ERROR_MESSAGE,
        INTERNAL_ERROR
    }

}
