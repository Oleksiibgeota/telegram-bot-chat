package com.chatbot.core.repositories.impl.operation;

import com.chatbot.core.domain.TelegramAccount;
import com.chatbot.core.repositories.TelegramAccountRepo;
import com.chatbot.core.repositories.generic.GenericOperationsImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TelegramAccountRepoImpl extends GenericOperationsImpl implements TelegramAccountRepo {
    @Override
    public String getTableName() {
        return "telegram_user";
    }

    @Override
    public Long create(TelegramAccount account) {
        String sql = "INSERT INTO telegram_user (external_id, first_name, last_name, user_name)" +
                " VALUES (:external_id, :first_name, :last_name, :user_name) returning *";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("external_id", account.getExternalId())
                .addValue("first_name", account.getFirstName())
                .addValue("last_name", account.getLastName())
                .addValue("user_name", account.getUserName()
                );
        return insertAndReturn(sql, source, this.getRowMapper()).getId();
    }

    @Override
    public Optional<TelegramAccount> findByExternalId(Long externalId) {
        String sql = "SELECT * FROM telegram_user WHERE external_id = :external_id";
        return findAnyObject(sql, compose("external_id", externalId), getRowMapper());
    }


    public RowMapper<TelegramAccount> getRowMapper() {
        return (rs, rowNum) -> TelegramAccount.builder()
                .id(rs.getLong("id"))
                .externalId(rs.getLong("external_id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .userName(rs.getString("user_name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
