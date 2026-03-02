package com.rev.app.config;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.rev.app.entity.User;
import com.rev.app.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private IUserRepository userRepository;

    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void testLoadUserByUsername_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setMasterPasswordHash("hashed");
        user.setEmailVerified(true);
        user.setAccountLocked(false);

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("testuser");

        assertNotNull(details);
        assertEquals("testuser", details.getUsername());
        assertTrue(details.isEnabled());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nonexistent"));
    }
}
