package com.rev.app.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.rev.app.entity.User;

@DataJpaTest
class IUserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;

    @Test
    void testSaveAndFindByUsername() {
        User user = User.builder()
                .username("testrepo")
                .email("repo@mail.com")
                .masterPasswordHash("hash")
                .build();
        
        userRepository.save(user);
        
        Optional<User> found = userRepository.findByUsername("testrepo");
        assertTrue(found.isPresent());
        assertEquals("repo@mail.com", found.get().getEmail());
    }

    @Test
    void testExists() {
        User user = User.builder()
                .username("exists")
                .email("exists@mail.com")
                .masterPasswordHash("hash")
                .build();
        userRepository.save(user);
        
        assertTrue(userRepository.existsByUsername("exists"));
        assertTrue(userRepository.existsByEmail("exists@mail.com"));
    }
}
