package com.lovegiver.training.optical.payload;

import java.util.Objects;
import java.util.StringJoiner;

public final class Message<T> {

    private final T payload;
    private boolean isError = false;

    public Message(T payload) {
        this.payload = payload;
    }

    public Message(T payload, boolean isError) {
        this.payload = payload;
        this.isError = isError;
    }

    public T payload() {
        return payload;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message<?> message = (Message<?>) o;
        return isError == message.isError && Objects.equals(payload, message.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload, isError);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Message.class.getSimpleName() + "[", "]")
                .add("payload=" + payload)
                .add("isError=" + isError)
                .toString();
    }
}
