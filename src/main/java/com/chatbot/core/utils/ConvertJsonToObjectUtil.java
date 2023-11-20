package com.chatbot.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConvertJsonToObjectUtil {
    private static final ObjectMapper mapper = new ObjectMapper();


    public static final <T> T getFromJSON(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException var3) {
            throw new RuntimeException(var3.getMessage(), var3);
        }
    }

    public static final <T> T getFromJSON(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException var3) {
            throw new RuntimeException(var3.getMessage(), var3);
        }
    }

    public static final <T> T getFromJSON(String root, String json, TypeReference<T> typeReference) {
        Map<String, Object> r = (Map) getFromJSON(json, Map.class);
        String jsonFromRoot = toJSON(r.get(root));
        return getFromJSON(jsonFromRoot, typeReference);
    }

    public static final <T> T getFromJSON(String root, String json, Class<T> clazz) {
        Map<String, Object> r = (Map) getFromJSON(json, Map.class);
        String jsonFromRoot = toJSON(r.get(root));
        return getFromJSON(jsonFromRoot, clazz);
    }

    public static final <T> String toJSON(T clazz) {
        try {
            return mapper.writeValueAsString(clazz);
        } catch (JsonProcessingException var2) {
            throw new RuntimeException(var2.getMessage(), var2);
        }
    }

    static {
        mapper.registerModule(new JavaTimeModule());
    }
}
