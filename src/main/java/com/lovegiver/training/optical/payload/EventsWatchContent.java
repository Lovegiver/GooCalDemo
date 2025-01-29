package com.lovegiver.training.optical.payload;

import java.util.Objects;
import java.util.StringJoiner;

public class EventsWatchContent {

    private String kind;
    private String id;
    private String resourceId;
    private String resourceUri;
    private String token;
    private long expiration;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EventsWatchContent.class.getSimpleName() + "[", "]")
                .add("kind='" + kind + "'")
                .add("id='" + id + "'")
                .add("resourceId='" + resourceId + "'")
                .add("resourceUri='" + resourceUri + "'")
                .add("nextSyncToken='" + token + "'")
                .add("expiration=" + expiration)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventsWatchContent that = (EventsWatchContent) o;
        return expiration == that.expiration && Objects.equals(kind, that.kind) && Objects.equals(id, that.id) && Objects.equals(resourceId, that.resourceId) && Objects.equals(resourceUri, that.resourceUri) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, id, resourceId, resourceUri, token, expiration);
    }
}
