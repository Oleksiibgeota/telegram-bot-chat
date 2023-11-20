package com.chatbot.core.repositories.impl.operation;

import com.chatbot.core.domain.Roles;
import com.chatbot.core.repositories.RoleManagerRepository;
import com.chatbot.core.repositories.generic.GenericOperationsImpl;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class RoleManagerRepoImpl extends GenericOperationsImpl implements RoleManagerRepository {

    @Override
    public void addAuthorities(Long userId, Roles.Type role) {
        String sql = "WITH select_roles_id AS (SELECT id FROM roles WHERE role_type = :role_type :: role_type_enum)" +
                " INSERT INTO roles_user_matcher (role_id, user_id)" +
                "  VALUES ((SELECT id FROM select_roles_id), :user_id)";
        executeAny(sql, new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("role_type", role.name()));
    }

    @Override
    public void deleteAuthorities(Long userId, Roles.Type role) {
        String sql = "DELETE FROM roles_user_matcher " +
                " WHERE role_id = (SELECT id FROM roles " +
                "                               WHERE role_type = :role_type :: role_type_enum)" +
                "   AND user_id = :user_id";
        executeAny(sql, new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("role_type", role.name()));
    }

    @Override
    public Set<String> getAuthoritiesByUserId(Long userId) {
        String sql = "SELECT r.role_type " +
                " FROM roles r " +
                "         JOIN roles_user_matcher rum ON r.id = rum.role_id " +
                " WHERE rum.user_id = :user_id";
        List<String> authorities = findAnyListPrimitiveType(sql, compose("user_id", userId), String.class);
        if (CollectionUtils.isEmpty(authorities)) {
            return new HashSet<>(List.of(Roles.Type.FOLLOWER.name()));
        } else return new HashSet<>(authorities);
    }

}
