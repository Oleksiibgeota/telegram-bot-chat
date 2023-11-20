package com.chatbot.core.repositories.generic;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public interface GenericRepository<E> extends GenericOperations {

    String PK_NAME = "id";

    default Optional<E> findById(long id) {
        return findByField(PK_NAME, id);
    }

    default Optional<E> findByField(String field, Object value) {
        return findByMap(Collections.singletonMap(field, value));
    }

    default Long findCountByField(String field, Object value) {
        return findCountByMap(Collections.singletonMap(field, value));
    }

    default Long findCount() {
        String sql = String.format("SELECT COUNT(*) FROM %s", getTableName());
        return findLong(sql, empty()).orElse(0L);
    }

    default Optional<E> findByMap(Map<String, Object> fieldToValue) {
        if (fieldToValue == null || fieldToValue.isEmpty()) {
            return Optional.empty();
        }

        String where = fieldToValue.entrySet().stream()
                .map(e -> resolveArg(e.getKey(), e.getValue()))
                .collect(Collectors.joining(" AND "));

        String sql = String.format("SELECT * FROM %s WHERE %s", getTableName(), where);
        return findObject(sql, new MapSqlParameterSource(fieldToValue));
    }

    default Long findCountByMap(Map<String, Object> fieldToValue) {
        if (fieldToValue == null || fieldToValue.isEmpty()) {
            return 0L;
        }

        String where = fieldToValue.entrySet().stream()
                .map(e -> resolveArg(e.getKey(), e.getValue()))
                .collect(Collectors.joining(" AND "));

        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s", getTableName(), where);
        return findLong(sql, new MapSqlParameterSource(fieldToValue))
                .orElse(0L);
    }

    default Optional<E> findObject(String sql, SqlParameterSource params) {
        return findAnyObject(sql, params, getRowMapper());
    }

    default List<E> findList(String sql) {
        return findAnyList(sql, empty(), getRowMapper());
    }

    default List<E> findListByIds(Collection<Long> ids) {
        return findListByField(PK_NAME, ids);
    }

    default List<E> findListByField(String field, Object value) {
        boolean collection = value instanceof Collection;
        if (collection && CollectionUtils.isEmpty((Collection<?>) value)) {
            return new ArrayList<>();
        }
        String sql = String.format("SELECT * FROM %s WHERE %s", getTableName(), resolveArg(field, value));
        return findList(sql, compose(field, value));
    }

    default List<E> findListByMap(Map<String, Object> fieldToValue, boolean page) {
        if (fieldToValue == null || fieldToValue.isEmpty()) {
            return new ArrayList<>();
        }

        String where = fieldToValue.entrySet().stream()
                .filter(field -> {
                    if (page) {
                        return !"limit".equals(StringUtils.lowerCase(field.getKey()))
                                && !"offset".equals(StringUtils.lowerCase(field.getKey()));
                    } else {
                        return true;
                    }
                })
                .map(e -> resolveArg(e.getKey(), e.getValue()))
                .collect(Collectors.joining(" AND "));

        String sql = String.format("SELECT * FROM %s %s %s %s",
                getTableName(),
                StringUtils.isNotBlank(where) ? "WHERE" : "",
                where,
                page ? "LIMIT :limit OFFSET :offset" : "");
        return findList(sql, new MapSqlParameterSource(fieldToValue));
    }

    default List<E> findListByMap(Map<String, Object> fieldToValue) {
        return findListByMap(fieldToValue, false);
    }

    default List<E> findList(String sql, SqlParameterSource params) {
        return findAnyList(sql, params, getRowMapper());
    }

    default int deleteById(long id) {
        String sql = String.format("DELETE FROM %s WHERE %s=:%s", getTableName(), PK_NAME, PK_NAME);
        return (int) execute(sql, compose(PK_NAME, id));
    }

    default long insert(final MapSqlParameterSource params) {
        String fields = String.join(",", params.getParameterNames());
        String values = Arrays.stream(params.getParameterNames()).
                map(e -> ":" + e).collect(joining(","));
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", getTableName(), fields, values);
        KeyHolder holder = new GeneratedKeyHolder();
        executeAny(sql, params, holder);
        return singleId(holder);
    }

    default long update(final MapSqlParameterSource params, final long id) {
        String kv = Arrays.stream(params.getParameterNames()).
                map(e -> e + "=:" + e).collect(joining(","));
        String sql = String.format("UPDATE %s SET %s WHERE %s=:%s", getTableName(), kv, PK_NAME, PK_NAME);
        MapSqlParameterSource copy = new MapSqlParameterSource(params.getValues());
        copy.addValue(PK_NAME, id);
        return execute(sql, copy);
    }

    default long execute(String sql, SqlParameterSource params) {
        return executeAny(sql, params);
    }

    String getTableName();

    RowMapper<E> getRowMapper();

    default List<Long> listIds(KeyHolder holder) {
        return holder.getKeyList().stream()
                .map(map -> Long.valueOf((Integer) map.get("id")))
                .collect(Collectors.toList());
    }

    default long singleId(KeyHolder holder) {
        return ((Number) holder.getKeys().get("id")).longValue();
    }

    default String resolveArg(String key, Object value) {
        return value instanceof Collection ?
                String.format(" %s IN (:%s) ", key, key) :
                String.format(" %s=:%s ", key, key);
    }

}
