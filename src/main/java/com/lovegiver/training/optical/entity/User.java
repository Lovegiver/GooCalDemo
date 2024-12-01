package com.lovegiver.training.optical.entity;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "test_user")
@UserDefinition
public class User extends PanacheEntity {
    @Username
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

    public static Uni<String> add(String username, String password, String role) {
        User user = new User();
        user.username = username;
        user.password = BcryptUtil.bcryptHash(password);
        user.role = role;
        user.uniqueId = UUID.randomUUID();
        user.persist();
        return Uni.createFrom().item(user.uniqueId.toString());
    }
}
