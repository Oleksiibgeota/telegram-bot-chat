package com.chatbot.core.services;

import com.chatbot.core.domain.ChatDialog;
import com.chatbot.core.domain.TelegramAccount;
import com.chatbot.core.dto.ChatDialogDTO;
import com.chatbot.core.exception.NotFoundException;
import com.chatbot.core.repositories.ChatDialogRepo;
import com.chatbot.core.repositories.TelegramAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatDialogLog {
    private final TelegramAccountRepo telegramAccountRepo;
    private final ChatDialogRepo chatDialogRepo;

    public List<ChatDialogDTO> getLogsByTelegramUserId(Long telegramUserId) {
        Optional<TelegramAccount> findByIdOpt = this.telegramAccountRepo.findById(telegramUserId);
        if (findByIdOpt.isEmpty()) {
            throw new NotFoundException(String.format("Account by id %d not found", telegramUserId));
        }
        return this.chatDialogRepo.getAllByTelegramUserId(telegramUserId)
                .stream()
                .map(convertToDTO)
                .collect(Collectors.toList());
    }

    private static Function<ChatDialog, ChatDialogDTO> convertToDTO = (model) -> ChatDialogDTO.builder()
            .id(model.getId())
            .telegramAccountId(model.getTelegramAccountId())
            .userRequest(model.getUserRequest())
            .botResponse(model.getBotResponse())
            .createdAt(model.getCreatedAt())
            .build();
}
