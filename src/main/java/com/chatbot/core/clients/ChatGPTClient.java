package com.chatbot.core.clients;

import com.chatbot.core.utils.ConvertJsonToObjectUtil;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ChatGPTClient {
    @SneakyThrows
    public static String chatGPT(String content) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "X-GPT-API-KEY";

        String json = ConvertJsonToObjectUtil.toJSON(
                ChatGPTRequest.builder()
                        .messages(Collections.singletonList(
                                Message.builder()
                                        .content(content)
                                        .build()
                        ))
                        .build()
        );
        log.info("JSON {}", json);
        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            return extractMessageFromJSONResponse(response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content") + 11;

        int end = response.indexOf("\"", start);

        return response.substring(start, end);

    }

    @Data
    @Builder
    private static class ChatGPTRequest {
        private final String model = "gpt-3.5-turbo-1106";
        private List<Message> messages;
    }

    @Data
    @Builder
    private static class Message {
        private final String role = "user";
        private String content;
    }
}
