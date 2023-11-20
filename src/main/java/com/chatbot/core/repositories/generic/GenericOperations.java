package com.chatbot.core.repositories.generic;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GenericOperations {

    Optional<Integer> findInt(String sql, SqlParameterSource params);

    Optional<Long> findLong(String sql, SqlParameterSource params);

    Optional<String> findString(String sql, SqlParameterSource params);

    Optional<Boolean> findBoolean(String sql, SqlParameterSource params);

    Optional<BigDecimal> findBigDecimal(String sql, SqlParameterSource params);

    <T> Optional<T> findPrimitiveType(String sql, SqlParameterSource params, Class<T> cls);

    <E> Optional<E> findAnyObject(String sql, SqlParameterSource params, RowMapper<E> rowMapper);

    <E> List<E> findAnyList(String sql, SqlParameterSource params, RowMapper<E> rowMapper);

    <E> List<E> findAnyListPrimitiveType(String sql, SqlParameterSource params, Class<E> cls);

    <E> E insertAndReturn(String sql, SqlParameterSource params, RowMapper<E> rowMapper);

    long executeAny(String sql, SqlParameterSource params);

    long executeAny(String sql, SqlParameterSource params, KeyHolder holder);

    void processQuery(String sql, SqlParameterSource params, RowCallbackHandler rch);

    default MapSqlParameterSource compose(String key, Object value) {
        return new MapSqlParameterSource().addValue(key, value);
    }

    default EmptySqlParameterSource empty() {
        return EmptySqlParameterSource.INSTANCE;
    }


}
