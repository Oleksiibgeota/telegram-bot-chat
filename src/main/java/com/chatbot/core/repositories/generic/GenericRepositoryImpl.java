package com.chatbot.core.repositories.generic;

public abstract class GenericRepositoryImpl<E> extends GenericOperationsImpl implements GenericRepository<E> {

    private final String tableName;

    public GenericRepositoryImpl(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

}
