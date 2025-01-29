package com.lovegiver.training.optical.service;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.lovegiver.training.optical.entity.User;
import com.lovegiver.training.optical.exception.TechnicalException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class DbDataStoreFactory extends AbstractDataStoreFactory {

    @Inject
    public DbDataStoreFactory() {

    }

    @Override
    protected <V extends Serializable> DataStore<V> createDataStore(String storeId) {
        return new DbDataStore<>(this, storeId);
    }

    static class DbDataStore<V extends Serializable> extends AbstractDbDataStore<V> {

        protected DbDataStore(
                DataStoreFactory dataStoreFactory,
                String storeId
        ) {
            super(dataStoreFactory, storeId);
            if (super.keyValueMap.isEmpty()) {
                initialize();
            }
        }

        void initialize() {
            List<User> allUsers = User.listAll();
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
            @NotNull Optional<User> optionalUser = User.findByUniqueId(UUID.fromString(uuid));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.accessToken = ((StoredCredential) storedCredential).getAccessToken();
                user.refreshToken = ((StoredCredential) storedCredential).getRefreshToken();
                user.tokenExpiry = ((StoredCredential) storedCredential).getExpirationTimeMilliseconds();
                user.persistAndFlush();
            }

        }
    }
}
