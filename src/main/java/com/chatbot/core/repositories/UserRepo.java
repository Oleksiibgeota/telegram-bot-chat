package com.chatbot.core.repositories;

import com.chatbot.core.domain.User;
import com.chatbot.core.repositories.generic.GenericOperations;

import java.util.Optional;

public interface UserRepo extends GenericOperations {

    long save(User user);

    Optional<User> getById(Long userId);

    User getByIdOrElseThrow(Long id);

    Optional<User> getByUuid(String uuid);

    User getByUuidOrElseThrow(String uuid);

    Optional<User> findByMail(String email);

}
