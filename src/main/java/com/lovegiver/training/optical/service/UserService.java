package com.lovegiver.training.optical.service;

import com.lovegiver.training.optical.entity.User;
import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.payload.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface UserService {

    @NotNull Message<String> addUser(Credentials credentials);

    @NotNull Optional<User> findByUsername(String username);

    @NotNull Optional<User> findByUsernameAndPassword(Credentials credentials);
}
