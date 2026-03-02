package main.test.config;

import com.rev.app.config.UserDetailsServiceImpl;
import com.rev.app.entity.User;
import com.rev.app.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    @Mock
    private IUserRepository userRepository;

    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
