package com.lovegiver.training.optical.service;

import com.lovegiver.training.optical.entity.User;
import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.payload.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private static final String ROLE = "user";

    public UserServiceImpl() {
    }

    @Override
    @Transactional
    public @NotNull Message<String> addUser(@NotNull Credentials credentials) {
        if (this.findByUsername(credentials.getLogin()).isPresent()) {
            return new Message<>("Username already exists", true);
        }
        return User.createUserWithRole(credentials.getLogin(), credentials.getPassword(), ROLE);
    }

    @Override
    @Transactional
    public @NotNull Optional<User> findByUsername(@NotNull String username) {
        return User.findByUsername(username);
    }

    @Override
    @Transactional
    public @NotNull Optional<User> findByUsernameAndPassword(@NotNull Credentials credentials) {
        return User.findByUsernameAndPassword(credentials.getLogin(), credentials.getPassword());
    }
}
