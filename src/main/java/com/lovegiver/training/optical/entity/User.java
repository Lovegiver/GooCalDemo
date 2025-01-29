package com.lovegiver.training.optical.entity;

import com.lovegiver.training.optical.payload.Message;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(name = "opt_user")
@UserDefinition
public class User extends PanacheEntity {
    @Username
    @Column(unique = true)
    public String username;
    @Password
    public String password;
    @Roles
    public String role;
    @Column(unique = true)
    public UUID uniqueId;
    @Column
    public String googleUserId;
    @Column
    public String accessToken;
    @Column
    public String refreshToken;
    @Column
    public long tokenExpiry;

    private User(@NotNull Builder builder) {
        username = builder.username;
        password = builder.password;
        role = builder.role;
        uniqueId = builder.uniqueId;
    }

    protected User() {

    }

    @Contract("_, _, _ -> new")
    public static @NotNull Message<String> createUserWithRole(String username, String password, String role) {
        User user = Builder.builder()
                .username(username)
                .password(BcryptUtil.bcryptHash(password))
                .role(role)
                .uniqueId(UUID.randomUUID())
                .build();
        user.persist();
        return new Message<>(user.uniqueId.toString());
    }

    public static @NotNull Optional<User> findByUsername(String username) {
        return Optional.ofNullable(find("username", username).firstResult());
    }

    public static @NotNull Optional<User> findByUsernameAndPassword(String username, String password) {
        User user = find("username", username).firstResult();
        if (user != null) {
            if (BcryptUtil.matches(password, user.password)) {
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<User> findByUniqueId(UUID uniqueId) {
        return Optional.ofNullable(find("uniqueId", uniqueId).firstResult());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uniqueId, user.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uniqueId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("role='" + role + "'")
                .add("uniqueId=" + uniqueId)
                .add("googleUserId='" + googleUserId + "'")
                .add("accessToken='" + accessToken + "'")
                .add("refreshToken='" + refreshToken + "'")
                .add("tokenExpiry=" + tokenExpiry)
                .add("id=" + id)
                .toString();
    }

    public static final class Builder {
        private String username;
        private String password;
        private String role;
        private UUID uniqueId;

        private Builder() {
        }

        @Contract(value = " -> new", pure = true)
        public static @NotNull Builder builder() {
            return new Builder();
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder role(String val) {
            role = val;
            return this;
        }

        public Builder uniqueId(UUID val) {
            uniqueId = val;
            return this;
        }

        @Contract(" -> new")
        public @NotNull User build() {
            return new User(this);
        }
    }
}
