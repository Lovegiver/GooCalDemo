package com.lovegiver.training.optical.service;

import com.google.api.client.util.IOUtils;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AbstractDbDataStore<V extends Serializable> extends AbstractDataStore<V> {

    private final Lock lock = new ReentrantLock();
    protected HashMap<String, byte[]> keyValueMap = Maps.newHashMap();

    protected AbstractDbDataStore(DataStoreFactory dataStoreFactory, String id) {
        super(dataStoreFactory, id);
    }

    @Override
    public Set<String> keySet() {
        this.lock.lock();

        Set<String> uuids;
        try {
            uuids = Collections.unmodifiableSet(this.keyValueMap.keySet());
        } finally {
            this.lock.unlock();
        }

        return uuids;
    }

    @Override
    public Collection<V> values() throws IOException {
        this.lock.lock();

        List<V> storedCredentials;
        try {
            List<V> result = Lists.newArrayList();

            for(byte[] bytes : this.keyValueMap.values()) {
                result.add(IOUtils.deserialize(bytes));
            }

            storedCredentials = Collections.unmodifiableList(result);
        } finally {
            this.lock.unlock();
        }

        return storedCredentials;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(String uuid) throws IOException {
        if (uuid == null) {
            return null;
        } else {
            this.lock.lock();

            Serializable storedCredential;
            try {
                storedCredential = IOUtils.deserialize(this.keyValueMap.get(uuid));
            } finally {
                this.lock.unlock();
            }

            return (V)storedCredential;
        }
    }

    @Override
    @Transactional
    public DataStore<V> set(String uuid, V storedCredential) throws IOException {
        Preconditions.checkNotNull(uuid);
        Preconditions.checkNotNull(storedCredential);
        this.lock.lock();

        try {
            this.keyValueMap.put(uuid, IOUtils.serialize(storedCredential));
            this.save(uuid, storedCredential);
        } finally {
            this.lock.unlock();
        }

        return this;
    }

    @Override
    public DataStore<V> clear() {
        this.lock.lock();

        try {
            this.keyValueMap.clear();
        } finally {
            this.lock.unlock();
        }

        return this;
    }

    @Override
    public DataStore<V> delete(String uuid) {
        if (uuid != null) {
            this.lock.lock();

            try {
                this.keyValueMap.remove(uuid);
            } finally {
                this.lock.unlock();
            }

        }
        return this;
    }

    @Transactional
    public void save(String uuid, V storedCredential) {
    }
}
