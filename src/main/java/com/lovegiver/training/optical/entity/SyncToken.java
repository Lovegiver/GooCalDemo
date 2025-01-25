package com.lovegiver.training.optical.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(name = "opt_synctoken")
public class SyncToken extends PanacheEntity {
    @Column(unique = true, nullable = false)
    public UUID userUuid;
    @Column(unique = true, nullable = false)
    public String token;
    @Column
    public Timestamp received;
    @Column
    public long tokenExpiry;
    @Column
    public Timestamp lastUsed;

    public static SyncToken findByUserUuid(UUID uuid) {
        return find("userUuid", uuid).firstResult();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SyncToken syncToken = (SyncToken) o;
        return tokenExpiry == syncToken.tokenExpiry && Objects.equals(userUuid, syncToken.userUuid) && Objects.equals(token, syncToken.token) && Objects.equals(received, syncToken.received) && Objects.equals(lastUsed, syncToken.lastUsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userUuid, token, received, tokenExpiry, lastUsed);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SyncToken.class.getSimpleName() + "[", "]")
                .add("userUuid=" + userUuid)
                .add("token='" + token + "'")
                .add("received=" + received)
                .add("tokenExpiry=" + tokenExpiry)
                .add("lastUsed=" + lastUsed)
                .add("id=" + id)
                .toString();
    }
}
