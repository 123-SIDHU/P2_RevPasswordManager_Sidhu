package com.rev.app.service;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.rev.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.User;
import com.rev.app.exception.ValidationException;
import com.rev.app.mapper.SecurityQuestionMapper;
import com.rev.app.mapper.UserMapper;
import com.rev.app.repository.ISecurityQuestionRepository;
import com.rev.app.repository.IUserRepository;
import com.rev.app.service.IVerificationService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private ISecurityQuestionRepository sqRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IVerificationService verificationService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private SecurityQuestionMapper securityQuestionMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, sqRepository, passwordEncoder, verificationService, userMapper, securityQuestionMapper);
    }

    @Test
    void testPreValidateRegistration_PasswordsDoNotMatch() {
        RegisterDTO dto = new RegisterDTO();
        dto.setMasterPassword("p1");
        dto.setConfirmPassword("p2");

        assertThrows(ValidationException.class, () -> userService.preValidateRegistration(dto));
    }

    @Test
    void testRegister_Success() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("user");
        dto.setEmail("user@mail.com");
        dto.setMasterPassword("pass");
        dto.setConfirmPassword("pass");
        dto.setSecurityQuestions(Collections.nCopies(3, any()));

        User user = new User();
        user.setUsername("user");

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@mail.com")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User saved = userService.register(dto);

        assertNotNull(saved);
        assertEquals("user", saved.getUsername());
        verify(userRepository).save(any(User.class));
    }
}
