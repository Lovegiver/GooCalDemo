package com.lovegiver.training.optical.repository;

import com.lovegiver.training.optical.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

import java.util.UUID;

@Priority(1)
@Alternative
@ApplicationScoped
public class TestUserRepository implements PanacheRepository<User> {

    @PostConstruct
    void initUserRepository() {
        deleteAll();

        User fred = User.Builder.builder()
                .username("frederic.courcier@gmail.com")
                .password("pericard42")
                .role("user")
                .uniqueId(UUID.randomUUID())
                .build();

        User carole = User.Builder.builder()
                .username("carole.courcier@gmail.com")
                .password("pericard42")
                .role("user")
                .uniqueId(UUID.randomUUID())
                .build();

        persist(fred, carole);
    }

}
