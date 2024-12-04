package com.lovegiver.training.optical.service;

import com.lovegiver.training.optical.entity.User;
import com.lovegiver.training.optical.payload.Credentials;

public interface UserService {

    String addUser(Credentials credentials);

    User findByUsername(String username);
}
