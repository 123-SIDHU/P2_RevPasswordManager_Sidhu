package main.test.service.impl;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.User;
import com.rev.app.mapper.SecurityQuestionMapper;
import com.rev.app.mapper.UserMapper;
import com.rev.app.repository.ISecurityQuestionRepository;
import com.rev.app.repository.IUserRepository;
import com.rev.app.service.IVerificationService;
import com.rev.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, sqRepository, passwordEncoder, verificationService, userMapper, securityQuestionMapper);
    }

    @Test
    void testRegister_Success() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@mail.com");
        dto.setMasterPassword("pass");
        dto.setConfirmPassword("pass");
        dto.setSecurityQuestions(Collections.nCopies(3, new com.rev.app.dto.SecurityQuestionDTO()));

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        
        User user = new User();
        user.setId(1L);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(dto);
        
        assertNotNull(result);
        verify(userRepository).save(any());
        verify(sqRepository).saveAll(anyList());
    }

    @Test
    void testVerifyMasterPassword() {
        User user = User.builder().masterPasswordHash("hash").build();
        when(passwordEncoder.matches("raw", "hash")).thenReturn(true);
        
        assertTrue(userService.verifyMasterPassword(user, "raw"));
    }
}
