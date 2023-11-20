package com.chatbot.core.repositories;

import com.chatbot.core.domain.ChatDialog;

import java.util.List;

public interface ChatDialogRepo {

    void create(ChatDialog dialog);

    List<ChatDialog> getAllByTelegramUserId(Long telegramUserId);

}
