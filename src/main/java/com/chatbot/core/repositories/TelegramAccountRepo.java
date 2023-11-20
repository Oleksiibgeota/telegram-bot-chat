package com.chatbot.core.repositories;

import com.chatbot.core.domain.TelegramAccount;
import com.chatbot.core.repositories.generic.GenericRepository;

import java.util.Optional;

public interface TelegramAccountRepo extends GenericRepository<TelegramAccount> {

    Long create(TelegramAccount account);

    Optional<TelegramAccount> findByExternalId(Long externalId);

}
