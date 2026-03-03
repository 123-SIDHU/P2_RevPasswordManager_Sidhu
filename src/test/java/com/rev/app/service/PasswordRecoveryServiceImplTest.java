package com.rev.app.service;

import com.rev.app.entity.User;
import com.rev.app.exception.InvalidCredentialsException;
import com.rev.app.repository.ISecurityQuestionRepository;
import com.rev.app.repository.IUserRepository;
import com.rev.app.service.impl.PasswordRecoveryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceImplTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private ISecurityQuestionRepository sqRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordRecoveryServiceImpl recoveryService;

    @BeforeEach
    void setUp() {
        recoveryService = new PasswordRecoveryServiceImpl(userRepository, sqRepository, passwordEncoder);
    }

    @Test
    void testGetQuestions_UserNotFound() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> recoveryService.getQuestions("unknown"));
    }

    @Test
    void testResetPassword_Success() {
        User user = new User();
        user.setUsername("user");
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass123")).thenReturn("hashed");

        recoveryService.resetPassword("user", "newpass123");

        assertEquals("hashed", user.getMasterPasswordHash());
        verify(userRepository).save(user);
    }
}
