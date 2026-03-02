package main.test.service.impl;

import com.rev.app.entity.User;
import com.rev.app.entity.VerificationCode;
import com.rev.app.repository.IVerificationCodeRepository;
import com.rev.app.service.IEmailService;
import com.rev.app.service.impl.VerificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VerificationServiceImplTest {

    @Mock
    private IVerificationCodeRepository vcRepo;
    @Mock
    private IEmailService emailService;

    private VerificationServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new VerificationServiceImpl(vcRepo, emailService);
        ReflectionTestUtils.setField(service, "expiryMinutes", 10);
    }

    @Test
    void testGenerateAndSendOtp() {
        User user = User.builder().email("test@mail.com").build();
        String code = service.generateAndSendOtp(user, "2FA");
        
        assertNotNull(code);
        assertEquals(6, code.length());
        verify(vcRepo).save(any(VerificationCode.class));
        verify(emailService).sendOtp(eq("test@mail.com"), eq(code), eq("2FA"));
    }

    @Test
    void testValidateCode_Success() {
        User user = User.builder().id(1L).build();
        VerificationCode vc = VerificationCode.builder()
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        
        when(vcRepo.findTopByUserIdAndPurposeAndUsedFalseOrderByCreatedAtDesc(1L, "2FA"))
                .thenReturn(Optional.of(vc));

        assertTrue(service.validateCode(user, "123456", "2FA"));
        assertTrue(vc.isUsed());
    }
}
