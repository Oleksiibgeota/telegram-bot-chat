package com.chatbot.core.repositories.generic;

import lombok.SneakyThrows;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GenericOperationsImpl extends Operations implements GenericOperations {

    @Override
    public <E> List<E> findAnyList(String sql, SqlParameterSource params, RowMapper<E> rowMapper) {
        try {
            return operations.query(sql, params, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public <E> E insertAndReturn(String sql, SqlParameterSource params, RowMapper<E> rowMapper) {
        try {
            return operations.queryForObject(sql, params, rowMapper);
        } catch (DataAccessException unhandled) {
            throw unhandled;
        }
    }

    public <E> E updateAndReturn(String sql, SqlParameterSource params, RowMapper<E> rowMapper) {
        try {
            return operations.queryForObject(sql, params, rowMapper);
        } catch (DataAccessException unhandled) {
            throw unhandled;
        }
    }

    @SneakyThrows
    public static boolean hasColumn(ResultSet rs, String columnName) {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equals(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <E> List<E> findAnyListPrimitiveType(String sql, SqlParameterSource params, Class<E> cls) {
        try {
            return operations.queryForList(sql, params, cls);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Integer> findInt(String sql, SqlParameterSource params) {
        return findPrimitiveType(sql, params, Integer.class);
    }

    @Override
    public Optional<Long> findLong(String sql, SqlParameterSource params) {
        return findPrimitiveType(sql, params, Long.class);
    }

    @Override
    public Optional<String> findString(String sql, SqlParameterSource params) {
        return findPrimitiveType(sql, params, String.class);
    }

    @Override
    public Optional<Boolean> findBoolean(String sql, SqlParameterSource params) {
        return findPrimitiveType(sql, params, Boolean.class);
    }

    @Override
    public Optional<BigDecimal> findBigDecimal(String sql, SqlParameterSource params) {
        return findPrimitiveType(sql, params, BigDecimal.class);
    }

    @Override
    public <T> Optional<T> findPrimitiveType(String sql, SqlParameterSource params, Class<T> cls) {
        try {
            return Optional.ofNullable(operations.queryForObject(sql, params, cls));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException unhandled) {
            throw unhandled;
        }
    }

    @Override
    public <E> Optional<E> findAnyObject(String sql, SqlParameterSource params, RowMapper<E> rowMapper) {
        try {
            return Optional.ofNullable(operations.queryForObject(sql, params, rowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (IncorrectResultSizeDataAccessException unhandled) {
            throw unhandled;
        }
    }

    @Override
    public void processQuery(String sql, SqlParameterSource params, RowCallbackHandler rch) {
        operations.query(sql, params, rch);
    }

    @Override
    public long executeAny(String sql, SqlParameterSource params) {
        return operations.update(sql, params);
    }

    @Override
    public long executeAny(String sql, SqlParameterSource params, KeyHolder holder) {
        return operations.update(sql, params, holder);
    }
}
