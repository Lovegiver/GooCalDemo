package com.lovegiver.training.optical.service;

import com.lovegiver.training.optical.entity.User;
import com.lovegiver.training.optical.payload.Credentials;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private static final String ROLE = "user";

    @Override
    @Transactional
    public Uni<String> addUser(Credentials credentials) {
        return User.add(credentials.getLogin(), credentials.getPassword(), ROLE);
    }
}
