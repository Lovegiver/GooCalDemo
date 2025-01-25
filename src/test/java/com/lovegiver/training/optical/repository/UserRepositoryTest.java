package com.lovegiver.training.optical.repository;

import com.lovegiver.training.optical.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * For these unit tests, data are stored temporarily in {@link User} repository.
 * Details in {@link TestUserRepository#initUserRepository()}
 */
@QuarkusTest
class UserRepositoryTest {

    private static final Logger LOG = Logger.getLogger(UserRepositoryTest.class);

    @Test
    @Transactional
    void whenListAllUsers_thenGetTwoUsers() {
        List<User> users = User.findAll().list();
        assertEquals(2, users.size());
    }

    /**
     * {@link User#username} has a UNIQUE constraint
     */
    @Test
    @Transactional
    void whenCreateUserWithRoleExistingUser_thenThrowException() {
        User fred = new User();
        fred.username = "frederic.courcier@gmail.com";
        fred.password = "pericard42";
        fred.role = "user";
        fred.uniqueId = UUID.randomUUID();
        assertThrows(ConstraintViolationException.class, fred::persistAndFlush);
    }

}