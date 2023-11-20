package com.chatbot.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ShowRequestsConfiguration {

    private @Value("${delays-tracking.sql:3000}")
    long slowSQLThreshold; // ms

    @Bean
    @Primary
    public NamedParameterJdbcOperations getNamedParameterJdbcTemplate(DataSource dataSource) {
        NamedParameterJdbcTemplate original = new NamedParameterJdbcTemplate(dataSource);
        return new NamedParameterJdbcOperations() {
            @Override
            public @NotNull JdbcOperations getJdbcOperations() {
                return original.getJdbcOperations();
            }

            @Override
            public <T> T execute(@NotNull String sql, @NotNull SqlParameterSource paramSource, PreparedStatementCallback<T> action) throws DataAccessException {
                return traceCall(() -> original.execute(sql, paramSource, action), sql, paramSource);
            }

            @Override
            public <T> T execute(@NotNull String sql, @NotNull Map<String, ?> paramMap, PreparedStatementCallback<T> action) throws DataAccessException {
                return traceCall(() -> original.execute(sql, paramMap, action), sql, paramMap);
            }

            @Override
            public <T> T execute(@NotNull String sql, @NotNull PreparedStatementCallback<T> action) throws DataAccessException {
                return traceCall(() -> original.execute(sql, action), sql, null);
            }

            @Override
            public <T> T query(@NotNull String sql, @NotNull SqlParameterSource paramSource, ResultSetExtractor<T> rse) throws DataAccessException {
                return traceCall(() -> original.query(sql, paramSource, rse), sql, paramSource);
            }

            @Override
            public <T> T query(@NotNull String sql, Map<String, ?> paramMap, ResultSetExtractor<T> rse) throws DataAccessException {
                return traceCall(() -> original.query(sql, paramMap, rse), sql, paramMap);
            }

            @Override
            public <T> T query(@NotNull String sql, ResultSetExtractor<T> rse) throws DataAccessException {
                return traceCall(() -> original.query(sql, rse), sql, null);
            }

            @Override
            public void query(@NotNull String sql, @NotNull SqlParameterSource paramSource, RowCallbackHandler rch) throws DataAccessException {
                traceCall(() -> {
                    original.query(sql, paramSource, rch);
                    return null;
                }, sql, paramSource);
            }

            @Override
            public void query(@NotNull String sql, Map<String, ?> paramMap, RowCallbackHandler rch) throws DataAccessException {
                traceCall(() -> {
                    original.query(sql, paramMap, rch);
                    return null;
                }, sql, paramMap);
            }

            @Override
            public void query(@NotNull String sql, RowCallbackHandler rch) throws DataAccessException {
                traceCall(() -> {
                    original.query(sql, rch);
                    return null;
                }, sql, null);
            }

            @Override
            public <T> List<T> query(@NotNull String sql, @NotNull SqlParameterSource paramSource, @NotNull  RowMapper<T> rowMapper) throws DataAccessException {
                return traceCall(() -> original.query(sql, paramSource, rowMapper), sql, paramSource);
            }

            @Override
            public <T> List<T> query(@NotNull String sql, @NotNull Map<String, ?> paramMap, @NotNull RowMapper<T> rowMapper) throws DataAccessException {
                return traceCall(() -> original.query(sql, paramMap, rowMapper), sql, paramMap);
            }

            @Override
            public <T> @NotNull List<T> query(@NotNull String sql, @NotNull RowMapper<T> rowMapper) throws DataAccessException {
                return traceCall(() -> original.query(sql, rowMapper), sql, null);
            }

            @Override
            public <T> Stream<T> queryForStream(@NotNull String sql, @NotNull SqlParameterSource paramSource, @NotNull  RowMapper<T> rowMapper) throws DataAccessException {
                //todo
                return null;
            }

            @Override
            public <T> Stream<T> queryForStream(@NotNull String sql, Map<String, ?> paramMap, @NotNull  RowMapper<T> rowMapper) throws DataAccessException {
                //todo
                return null;
            }

            @Override
            public <T> T queryForObject(@NotNull String sql, @NotNull SqlParameterSource paramSource, @NotNull RowMapper<T> rowMapper) throws DataAccessException {
                return traceCall(() -> original.queryForObject(sql, paramSource, rowMapper), sql, paramSource);
            }

            @Override
            public <T> T queryForObject(@NotNull String sql, @NotNull Map<String, ?> paramMap, @NotNull RowMapper<T> rowMapper) throws DataAccessException {
                return traceCall(() -> original.queryForObject(sql, paramMap, rowMapper), sql, paramMap);
            }

            @Override
            public <T> T queryForObject(@NotNull String sql, @NotNull SqlParameterSource paramSource, @NotNull Class<T> requiredType) throws DataAccessException {
                return traceCall(() -> original.queryForObject(sql, paramSource, requiredType), sql, paramSource);
            }

            @Override
            public <T> T queryForObject(@NotNull String sql, @NotNull Map<String, ?> paramMap, @NotNull Class<T> requiredType) throws DataAccessException {
                return traceCall(() -> original.queryForObject(sql, paramMap, requiredType), sql, paramMap);
            }

            @Override
            public @NotNull Map<String, Object> queryForMap(@NotNull String sql, @NotNull SqlParameterSource paramSource) throws DataAccessException {
                return traceCall(() -> original.queryForMap(sql, paramSource), sql, paramSource);
            }

            @Override
            public @NotNull Map<String, Object> queryForMap(@NotNull String sql, @NotNull Map<String, ?> paramMap) throws DataAccessException {
                return traceCall(() -> original.queryForMap(sql, paramMap), sql, paramMap);
            }

            @Override
            public <T> @NotNull List<T> queryForList(@NotNull String sql, @NotNull SqlParameterSource paramSource, @NotNull Class<T> elementType) throws DataAccessException {
                return traceCall(() -> original.queryForList(sql, paramSource, elementType), sql, paramSource);
            }

            @Override
            public <T> @NotNull List<T> queryForList(@NotNull String sql, @NotNull Map<String, ?> paramMap, @NotNull Class<T> elementType) throws DataAccessException {
                return traceCall(() -> original.queryForList(sql, paramMap, elementType), sql, paramMap);
            }

            @Override
            public @NotNull List<Map<String, Object>> queryForList(@NotNull String sql, @NotNull SqlParameterSource paramSource) throws DataAccessException {
                return traceCall(() -> original.queryForList(sql, paramSource), sql, paramSource);
            }

            @Override
            public @NotNull List<Map<String, Object>> queryForList(@NotNull String sql, @NotNull Map<String, ?> paramMap) throws DataAccessException {
                return traceCall(() -> original.queryForList(sql, paramMap), sql, paramMap);
            }

            @Override
            public @NotNull SqlRowSet queryForRowSet(@NotNull String sql, @NotNull SqlParameterSource paramSource) throws DataAccessException {
                return traceCall(() -> original.queryForRowSet(sql, paramSource), sql, paramSource);
            }

            @Override
            public @NotNull SqlRowSet queryForRowSet(@NotNull String sql, @NotNull Map<String, ?> paramMap) throws DataAccessException {
                return traceCall(() -> original.queryForRowSet(sql, paramMap), sql, paramMap);
            }

            @Override
            public int update(@NotNull String sql, @NotNull SqlParameterSource paramSource) throws DataAccessException {
                return traceCall(() -> original.update(sql, paramSource), sql, paramSource);
            }

            @Override
            public int update(@NotNull String sql, @NotNull Map<String, ?> paramMap) throws DataAccessException {
                return traceCall(() -> original.update(sql, paramMap), sql, paramMap);
            }

            @Override
            public int update(@NotNull String sql, @NotNull SqlParameterSource paramSource, @NotNull KeyHolder generatedKeyHolder) throws DataAccessException {
                return traceCall(() -> original.update(sql, paramSource, generatedKeyHolder), sql, paramSource);
            }

            @Override
            public int update(@NotNull String sql, @NotNull SqlParameterSource paramSource, @NotNull KeyHolder generatedKeyHolder, String @NotNull [] keyColumnNames) throws DataAccessException {
                return traceCall(() -> original.update(sql, paramSource, generatedKeyHolder, keyColumnNames), sql, paramSource);
            }

            @Override
            public int @NotNull [] batchUpdate(@NotNull String sql, Map<String, ?> @NotNull [] batchValues) {
                return traceCall(() -> original.batchUpdate(sql, batchValues), sql, batchValues);
            }

            @Override
            public int @NotNull [] batchUpdate(@NotNull String sql, SqlParameterSource @NotNull [] batchArgs) {
                return traceCall(() -> original.batchUpdate(sql, batchArgs), sql, batchArgs);
            }

            private <E> E traceCall(Supplier<E> e, String sql, Object args) {
                StopWatch sw = StopWatch.createStarted();
                E result = e.get();
                long ms = sw.getTime(TimeUnit.MILLISECONDS);
                if (ms >= slowSQLThreshold) {
                    args = args == null ? "[NULL]" : args;
                    log.error("SLOW-SQL call detected. Took {} ms-> {} with args {} ", ms, sql, args);
                }
                return result;
            }
        };
    }
}
