package com.chatbot.core.repositories.impl.operation;

import com.chatbot.core.domain.User;
import com.chatbot.core.exception.NotFoundException;
import com.chatbot.core.repositories.RoleManagerRepository;
import com.chatbot.core.repositories.UserRepo;
import com.chatbot.core.repositories.generic.GenericOperationsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepoImpl extends GenericOperationsImpl implements UserRepo {

    private final RoleManagerRepository roleManagerRepo;

    @Override
    public long save(User user) {
        String sql = "INSERT INTO users (public_name, last_login, date_joined, uuid, nick_name, password, email," +
                "                   is_agreed_to_receive_news, is_staff, is_active)" +
                " VALUES (:public_name, now_utc(), now_utc(), uuid_generate_v4(), :nick_name, :password, :email," +
                "        :is_agreed_to_receive_news, false, true)";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("password", user.getPassword())
                .addValue("email", user.getEmail());
        KeyHolder holder = new GeneratedKeyHolder();
        executeAny(sql, source, holder);
        return (Long) holder.getKeys().get("id");
    }

    @Override
    public Optional<User> getById(Long userId) {
        String sql = "SELECT u.*, u.id as u_id FROM users u WHERE u.id = :id";
        return findAnyObject(sql, compose("id", userId), getFullUserRowMapper());
    }

    @Override
    public User getByIdOrElseThrow(Long userId) {
        return this.getById(userId).orElseThrow(() -> {
            log.error("Account by id: {} not found", userId);
            return new NotFoundException(String.format("Account by id: %d not found, or inactive", userId));
        });
    }


    @Override
    public Optional<User> getByUuid(String uuid) {
        String sql = "SELECT u.*, u.id as u_id FROM users u WHERE u.uuid = :uuid";
        return findAnyObject(sql, compose("uuid", uuid), getFullUserRowMapper());
    }

    @Override
    public User getByUuidOrElseThrow(String uuid) {
        return this.getByUuid(uuid).orElseThrow(() -> {
            log.error("Account by uuid: {} not found", uuid);
            return new NotFoundException("Account NOT_FOUND");
        });
    }

    @Override
    public Optional<User> findByMail(String email) {
        String sql = "SELECT u.*, u.id as u_id FROM users u" +
                " WHERE u.email ILIKE :email";
        return findAnyObject(sql, compose("email", email), getFullUserRowMapper());
    }

    private RowMapper<User> getFullUserRowMapper() {
        return (rs, rowNum) -> userMapper(rs);
    }

    private User userMapper(ResultSet rs) throws SQLException {
        Set<String> authorities = this.roleManagerRepo.getAuthoritiesByUserId(rs.getLong("u_id"));
        return new User(
                rs.getLong("u_id"),
                rs.getString("uuid"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getBoolean("is_active"),
                authorities
        );
    }

    private RowMapper<User> getUserRowMapper() {
        return (rs, rowNum) -> userMapper(rs);
    }


}
