package main.test.util;

import com.rev.app.entity.User;
import com.rev.app.repository.IUserRepository;
import com.rev.app.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthUtilTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    private AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authUtil = new AuthUtil(userRepository);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetCurrentUser_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = authUtil.getCurrentUser();
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetCurrentUser_NoAuth() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertNull(authUtil.getCurrentUser());
    }

    @Test
    void testGetCurrentUserId() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");

        User user = new User();
        user.setId(123L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertEquals(123L, authUtil.getCurrentUserId());
    }
}
