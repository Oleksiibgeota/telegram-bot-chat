package com.chatbot.core.repositories.generic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;

@Component
public class Operations {

    protected NamedParameterJdbcOperations operations;

    @Autowired
    public void setOperations(@Qualifier("getNamedParameterJdbcTemplate")
                                      NamedParameterJdbcOperations operations) {
        this.operations = operations;
    }
}
