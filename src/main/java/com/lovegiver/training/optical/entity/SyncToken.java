package com.lovegiver.training.optical.entity;

import com.google.api.services.calendar.model.Channel;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(name = "opt_synctoken", indexes = {
        @Index(name = "idx_channel_user", columnList = "userUuid")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uni_user_calendar", columnNames = { "userUuid", "calendarId" })
})
public class SyncToken extends PanacheEntity {
    /** The {@link User#uniqueId} created at user's registration */
    @Column(nullable = false)
    public UUID userUuid;
    /** The ID of the Google calendar we want to watch */
    @Column(nullable = false)
    public String calendarId;
    /** This is the token to provide each time we want to sync the calendar */
    @Column(unique = true)
    public String nextSyncToken;
    /** This long value is generated when we first record the object */
    @Column
    public Timestamp createdAt;
    /** This long value is given by Google when registering the {@link com.google.api.services.calendar.model.Channel} */
    @Column
    public long tokenExpiry;
    /** This long value indicates when the nextSyncToken was used */
    @Column
    public Timestamp lastUsed;

    private SyncToken(@NotNull Builder builder) {
        userUuid = builder.userUuid;
        calendarId = builder.calendarId;
        nextSyncToken = builder.nextSyncToken;
        createdAt = builder.createdAt;
        tokenExpiry = builder.tokenExpiry;
        lastUsed = builder.lastUsed;
    }

    protected SyncToken() {

    }

    public SyncToken(Channel channel, String calendarId, String nextSyncToken) {
        String[] channelIdAndToken = channel.getId().split("_");
        this.userUuid = UUID.fromString(channelIdAndToken[0]);
        this.createdAt = new Timestamp(Long.parseLong(channelIdAndToken[1]));
        this.tokenExpiry = channel.getExpiration();
        this.calendarId = calendarId;
        this.nextSyncToken = nextSyncToken;
    }

    public static @NotNull Optional<List<SyncToken>> findByUserUuid(UUID uuid) {
        return Optional.ofNullable(find("userUuid", uuid).list());
    }

    public static @NotNull Optional<List<SyncToken>> findByUserUuidAndCalendarId(UUID uuid, String calendarId) {
        return Optional.ofNullable(find("userUuid = ?1 and calendarId = ?2", uuid, calendarId).list());
    }

    /**
     * The {@link SyncToken} becomes disposable when its expiry is less than the current time minus 1 hour
     * @return True if the Channel has to be refreshed
     */
    public boolean isDisposable() {
        return convertToZonedDateTime(this.tokenExpiry)
                .minusHours(1)
                .isBefore(ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SyncToken syncToken = (SyncToken) o;
        return tokenExpiry == syncToken.tokenExpiry && Objects.equals(userUuid, syncToken.userUuid) && Objects.equals(calendarId, syncToken.calendarId) && Objects.equals(nextSyncToken, syncToken.nextSyncToken) && Objects.equals(createdAt, syncToken.createdAt) && Objects.equals(lastUsed, syncToken.lastUsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userUuid, calendarId, nextSyncToken, createdAt, tokenExpiry, lastUsed);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SyncToken.class.getSimpleName() + "[", "]")
                .add("userUuid=" + userUuid)
                .add("calendarId='" + calendarId + "'")
                .add("nextSyncToken='" + nextSyncToken + "'")
                .add("createdAt=" + createdAt)
                .add("tokenExpiry=" + tokenExpiry)
                .add("lastUsed=" + lastUsed)
                .add("id=" + id)
                .toString();
    }

    public static final class Builder {
        private UUID userUuid;
        private String calendarId;
        private String nextSyncToken;
        private Timestamp createdAt;
        private long tokenExpiry;
        private Timestamp lastUsed;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder userUuid(UUID val) {
            userUuid = val;
            return this;
        }

        public Builder calendarId(String val) {
            calendarId = val;
            return this;
        }

        public Builder nextSyncToken(String val) {
            nextSyncToken = val;
            return this;
        }

        public Builder createdAt(Timestamp val) {
            createdAt = val;
            return this;
        }

        public Builder tokenExpiry(long val) {
            tokenExpiry = val;
            return this;
        }

        public Builder lastUsed(Timestamp val) {
            lastUsed = val;
            return this;
        }

        public SyncToken build() {
            return new SyncToken(this);
        }
    }

    private ZonedDateTime convertToZonedDateTime(long timestamp) {
        return ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.of("UTC")
        );
    }
}
