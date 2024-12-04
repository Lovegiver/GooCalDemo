package com.lovegiver.training.optical.exception;

import java.util.Objects;

public final class AuthenticationException extends RuntimeException {

    private final String message;

    public AuthenticationException(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AuthenticationException) obj;
        return Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "AuthenticationException[" +
                "message=" + message + ']';
    }

}
