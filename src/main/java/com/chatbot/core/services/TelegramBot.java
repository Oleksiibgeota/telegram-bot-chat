package com.chatbot.core.services;

import com.chatbot.core.domain.ChatDialog;
import com.chatbot.core.domain.TelegramAccount;
import com.chatbot.core.repositories.ChatDialogRepo;
import com.chatbot.core.repositories.TelegramAccountRepo;
import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

import static com.chatbot.core.clients.ChatGPTClient.chatGPT;

@Slf4j
public class TelegramBot extends AbilityBot {

    private final TelegramAccountRepo telegramAccountRepo;
    private final ChatDialogRepo chatDialogRepo;

    public TelegramBot(TelegramAccountRepo telegramAccountRepo, ChatDialogRepo chatDialogRepo) {
        super("1229008542:AAG1FLqV_EhQpEbcnDyPwJu-HOQGcsk2JZo", "hanna-test");
        this.telegramAccountRepo = telegramAccountRepo;
        this.chatDialogRepo = chatDialogRepo;
    }


    @Override
    public long creatorId() {
        return 707458620;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("{}", update);
        final Long telegramUserId;
        if (!update.hasMessage() || !update.getMessage().isUserMessage() || !update.getMessage().hasText())
            return;

        SendMessage snd = new SendMessage();
        snd.setChatId(update.getMessage().getChatId());
        String response = chatGPT(update.getMessage().getText());
        snd.setText(response);
        User user = update.getMessage().getFrom();
        Optional<TelegramAccount> existOpt = this.telegramAccountRepo.findByExternalId(user.getId());
        if (existOpt.isPresent()) {
            telegramUserId = existOpt.get().getId();
        } else {
            telegramUserId = this.telegramAccountRepo.create(
                    TelegramAccount.builder()
                            .externalId(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .userName(user.getUserName())
                            .build()
            );
        }
        this.chatDialogRepo.create(
                ChatDialog.builder()
                        .telegramAccountId(telegramUserId)
                        .userRequest(update.getMessage().getText())
                        .botResponse(response)
                        .build()
        );

        try {
            super.execute(snd);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
