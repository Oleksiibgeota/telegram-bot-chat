package com.chatbot.core.repositories.impl.operation;

import com.chatbot.core.domain.ChatDialog;
import com.chatbot.core.repositories.ChatDialogRepo;
import com.chatbot.core.repositories.generic.GenericOperationsImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatDialogRepoImpl extends GenericOperationsImpl implements ChatDialogRepo {

    @Override
    public void create(ChatDialog dialog) {
        String sql = "INSERT INTO chat_dialog (telegram_user_id, user_request, bot_response)" +
                " VALUES (:telegram_user_id, :user_request, :bot_response)";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("telegram_user_id", dialog.getTelegramAccountId())
                .addValue("user_request", dialog.getUserRequest())
                .addValue("bot_response", dialog.getBotResponse());
        executeAny(sql, source);
    }

    @Override
    public List<ChatDialog> getAllByTelegramUserId(Long telegramUserId) {
        String sql = "SELECT * FROM chat_dialog WHERE telegram_user_id = :telegram_user_id";
        return findAnyList(sql, compose("telegram_user_id", telegramUserId), getRowMapper());
    }

    public RowMapper<ChatDialog> getRowMapper() {
        return (rs, rowNum) -> ChatDialog.builder()
                .id(rs.getLong("id"))
                .telegramAccountId(rs.getLong("telegram_user_id"))
                .userRequest(rs.getString("user_request"))
                .botResponse(rs.getString("bot_response"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
