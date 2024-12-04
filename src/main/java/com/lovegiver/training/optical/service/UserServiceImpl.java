package com.lovegiver.training.optical.service;

import com.lovegiver.training.optical.entity.User;
import com.lovegiver.training.optical.payload.Credentials;
import com.lovegiver.training.optical.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final String ROLE = "user";

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public String addUser(Credentials credentials) {
        return User.add(credentials.getLogin(), credentials.getPassword(), ROLE);
    }

    @Override
    @Transactional
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
