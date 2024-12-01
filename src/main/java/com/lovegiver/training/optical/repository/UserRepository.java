package com.lovegiver.training.optical.repository;

import com.lovegiver.training.optical.entity.User;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Uni<User> findByUniqueId(String uuid) {
        return find("uniqueId", uuid).firstResult();
    }

    public Uni<User> findByGoogleUserId(String googleUserId) {
        return find("googleUserId", googleUserId).firstResult();
    }
}
