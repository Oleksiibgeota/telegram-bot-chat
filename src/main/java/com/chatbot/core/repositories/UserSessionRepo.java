package com.chatbot.core.repositories;


import com.chatbot.core.domain.UserSession;
import com.chatbot.core.repositories.generic.GenericRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepo extends GenericRepository<UserSession> {

    Optional<UserSession> getUserBySessionUuid(String sessionUuid);

    List<UserSession> getSessionsByUserId(long userId);

    void create(UserSession userSession);

    void deleteAllForUser(long userUuid);
}
