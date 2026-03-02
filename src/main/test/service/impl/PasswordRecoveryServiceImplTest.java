package main.test.service.impl;

import com.rev.app.entity.SecurityQuestion;
import com.rev.app.entity.User;
import com.rev.app.repository.ISecurityQuestionRepository;
import com.rev.app.repository.IUserRepository;
import com.rev.app.service.impl.PasswordRecoveryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PasswordRecoveryServiceImplTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private ISecurityQuestionRepository sqRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordRecoveryServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PasswordRecoveryServiceImpl(userRepository, sqRepository, passwordEncoder);
    }

    @Test
    void testGetQuestions() {
        User user = User.builder().id(1L).username("test").build();
        when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(user));
        when(sqRepository.findByUserId(1L)).thenReturn(Collections.singletonList(new SecurityQuestion()));

        List<SecurityQuestion> result = service.getQuestions("test");
        assertFalse(result.isEmpty());
    }

    @Test
    void testValidateAnswers_Success() {
        SecurityQuestion sq = SecurityQuestion.builder().answerHash("hashed").build();
        when(sqRepository.findByUserId(1L)).thenReturn(Collections.singletonList(sq));
        when(passwordEncoder.matches("ans", "hashed")).thenReturn(true);

        assertTrue(service.validateAnswers(1L, Collections.singletonList(" ANS ")));
    }

    @Test
    void testResetPassword() {
        User user = new User();
        when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass123")).thenReturn("newhash");

        service.resetPassword("test", "newpass123");
        
        assertEquals("newhash", user.getMasterPasswordHash());
        verify(userRepository).save(user);
    }
}
