package com.chatbot.core.config;

import com.chatbot.core.repositories.ChatDialogRepo;
import com.chatbot.core.repositories.TelegramAccountRepo;
import com.chatbot.core.services.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotRegistration {
    private final TelegramAccountRepo telegramAccountRepo;
    private final ChatDialogRepo chatDialogRepo;

    @PostConstruct
    void register() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot(telegramAccountRepo, chatDialogRepo));
        } catch (TelegramApiException e) {
            log.warn("botsApi.registerBot failed");
            e.printStackTrace();
        }
    }
}
