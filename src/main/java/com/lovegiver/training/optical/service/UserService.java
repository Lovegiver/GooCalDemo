package com.lovegiver.training.optical.service;

import com.lovegiver.training.optical.payload.Credentials;
import io.smallrye.mutiny.Uni;

public interface UserService {

    Uni<String> addUser(Credentials credentials);

}
