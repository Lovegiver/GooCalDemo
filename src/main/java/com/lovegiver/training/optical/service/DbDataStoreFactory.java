package com.lovegiver.training.optical.service;

import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.AbstractMemoryDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.lovegiver.training.optical.entity.User;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;

import java.io.IOException;
import java.io.Serializable;

public class DbDataStoreFactory extends AbstractDataStoreFactory {

    public DbDataStoreFactory(PanacheRepository<User> repository) {
    }

    @Override
    protected <V extends Serializable> DataStore<V> createDataStore(String s) throws IOException {
        return null;
    }

    static class DbDataStore<V extends Serializable> extends AbstractMemoryDataStore<V> {

        protected DbDataStore(DataStoreFactory dataStoreFactory, String id) {
            super(dataStoreFactory, id);
        }

    }
}
