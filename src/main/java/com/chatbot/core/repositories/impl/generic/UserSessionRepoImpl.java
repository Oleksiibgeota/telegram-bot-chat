package com.chatbot.core.repositories.impl.generic;

import com.chatbot.core.domain.UserSession;
import com.chatbot.core.repositories.UserSessionRepo;
import com.chatbot.core.repositories.generic.GenericRepositoryImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserSessionRepoImpl extends GenericRepositoryImpl<UserSession> implements UserSessionRepo {

    public UserSessionRepoImpl() {
        super("user_session");
    }

    @Override
    public Optional<UserSession> getUserBySessionUuid(String sessionUuid) {
        return findByField("session_uuid", sessionUuid);
    }

    @Override
    public List<UserSession> getSessionsByUserId(long userId) {
        String sql = "SELECT * FROM user_session WHERE user_id = :user_id";
        return findList(sql, compose("user_id", userId));
    }

    @Override
    public void create(UserSession userSession) {
        insert(new MapSqlParameterSource()
                .addValue("ip", userSession.getIp())
                .addValue("session_uuid", userSession.getSessionUuid())
                .addValue("user_id", userSession.getUserId())
                .addValue("created_at", userSession.getCreatedAt())
                .addValue("expired_at", userSession.getExpiredAt())
        );
    }

    @Override
    public void deleteAllForUser(long userId) {
        String sql = "DELETE FROM user_session WHERE user_id = :user_id";
        execute(sql, new MapSqlParameterSource()
                .addValue("user_id", userId));
    }

    @Override
    public RowMapper<UserSession> getRowMapper() {
        return (rs, rowNum) -> new UserSession(
                rs.getLong("id"),
                rs.getString("session_uuid"),
                rs.getLong("user_id"),
                rs.getString("ip"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("expired_at").toLocalDateTime()
        );
    }

}
