package main.test.service.impl;

import com.rev.app.service.impl.EmailServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImpl(mailSender);
        
        // Use reflection to set private field @Value("${app.mail.from}")
        Field fromField = EmailServiceImpl.class.getDeclaredField("fromAddress");
        fromField.setAccessible(true);
        fromField.set(emailService, "noreply@revpass.com");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendOtp() {
        emailService.sendOtp("test@mail.com", "123456", "2FA");
        verify(mailSender).send(any(MimeMessage.class));
    }
}
