package com.chatbot.core.repositories;


import com.chatbot.core.domain.Roles;

import java.util.Set;

public interface RoleManagerRepository {

    void addAuthorities(Long userId, Roles.Type role);

    void deleteAuthorities(Long userId, Roles.Type role);

    Set<String> getAuthoritiesByUserId(Long userId);
}
