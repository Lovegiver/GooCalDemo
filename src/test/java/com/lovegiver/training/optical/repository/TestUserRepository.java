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

        User fred = new User();
        fred.username = "frederic.courcier@gmail.com";
        fred.password = "pericard42";
        fred.role = "user";
        fred.uniqueId = UUID.randomUUID();

        User carole = new User();
        carole.username = "carole.courcier@gmail.com";
        carole.password = "pericard42";
        carole.role = "user";
        carole.uniqueId = UUID.randomUUID();

        persist(fred, carole);
    }

}
