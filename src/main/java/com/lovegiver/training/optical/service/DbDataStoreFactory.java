package com.lovegiver.training.optical.service;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.lovegiver.training.optical.entity.User;
import com.lovegiver.training.optical.exception.TechnicalException;
import com.lovegiver.training.optical.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DbDataStoreFactory extends AbstractDataStoreFactory {

    private final PanacheRepository<User> repository;

    @Inject
    public DbDataStoreFactory(PanacheRepository<User> repository) {
        this.repository = repository;
    }

    @Override
    protected <V extends Serializable> DataStore<V> createDataStore(String storeId) {
        return new DbDataStore<>(this, storeId, this.repository);
    }

    static class DbDataStore<V extends Serializable> extends AbstractDbDataStore<V> {

        private final PanacheRepository<User> repository;

        protected DbDataStore(
                DataStoreFactory dataStoreFactory,
                String storeId,
                PanacheRepository<User> repository
        ) {
            super(dataStoreFactory, storeId);
            if (repository == null) {
                throw new TechnicalException("Repository is null");
            }
            this.repository = repository;
            if (super.keyValueMap.isEmpty()) {
                initialize();
            }
        }

        void initialize() {
            List<User> allUsers = this.repository.listAll();
            allUsers.forEach(user -> {
                if (user.accessToken != null || user.refreshToken != null || user.tokenExpiry > 0) {
                    StoredCredential storedCredential = new StoredCredential();
                    storedCredential.setAccessToken(user.accessToken);
                    storedCredential.setRefreshToken(user.refreshToken);
                    storedCredential.setExpirationTimeMilliseconds(user.tokenExpiry);
                    try {
                        super.keyValueMap.put(user.uniqueId.toString(), IOUtils.serialize(storedCredential));
                    } catch (IOException e) {
                        throw new TechnicalException("Error while serializing data");
                    }
                }
            });
        }

        @Override
        @Transactional
        public void save(String uuid, V storedCredential) {
            User user = ((UserRepository) this.repository).findByUniqueId(UUID.fromString(uuid));
            user.setAccessToken(((StoredCredential) storedCredential).getAccessToken());
            user.setRefreshToken(((StoredCredential) storedCredential).getRefreshToken());
            user.setTokenExpiry(((StoredCredential) storedCredential).getExpirationTimeMilliseconds());
            user.persistAndFlush();
        }
    }
}
