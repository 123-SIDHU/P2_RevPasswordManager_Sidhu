package com.rev.app.util;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.rev.app.entity.User;
import com.rev.app.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
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
