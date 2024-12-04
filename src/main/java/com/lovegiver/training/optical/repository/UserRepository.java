package com.lovegiver.training.optical.repository;

import com.lovegiver.training.optical.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public User findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public User findByUniqueId(UUID uniqueId) {
        return find("uniqueId", uniqueId).firstResult();
    }
}
