package com.rev.app.service;

import com.rev.app.entity.User;
import com.rev.app.repository.IVerificationCodeRepository;
import com.rev.app.service.IEmailService;
import com.rev.app.service.impl.VerificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerificationServiceImplTest {

    @Mock
    private IVerificationCodeRepository vcRepo;
    @Mock
    private IEmailService emailService;

    private VerificationServiceImpl verificationService;

    @BeforeEach
    void setUp() {
        verificationService = new VerificationServiceImpl(vcRepo, emailService);
        ReflectionTestUtils.setField(verificationService, "expiryMinutes", 10);
    }

    @Test
    void testGenerateAndSendOtp() {
        User user = new User();
        user.setEmail("test@mail.com");

        String code = verificationService.generateAndSendOtp(user, "2FA");

        assertNotNull(code);
        verify(emailService).sendOtp(anyString(), anyString(), anyString());
        verify(vcRepo).save(any());
    }
}
