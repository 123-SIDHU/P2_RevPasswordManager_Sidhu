package main.test.config;

import com.rev.app.config.CustomAuthenticationSuccessHandler;
import com.rev.app.entity.User;
import com.rev.app.repository.IUserRepository;
import com.rev.app.service.IVerificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomAuthenticationSuccessHandlerTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IVerificationService verificationService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;
    @Mock
    private HttpSession session;

    private CustomAuthenticationSuccessHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new CustomAuthenticationSuccessHandler(userRepository, verificationService);
    }

    @Test
    void testOnAuthenticationSuccess_No2FA() throws IOException, ServletException {
        when(authentication.getName()).thenReturn("user1");
        User user = new User();
        user.setTotpEnabled(false);
        when(userRepository.findByUsernameOrEmail("user1", "user1")).thenReturn(Optional.of(user));

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("/dashboard");
    }

    @Test
    void testOnAuthenticationSuccess_With2FA() throws IOException, ServletException {
        when(authentication.getName()).thenReturn("user1");
        User user = User.builder().id(1L).username("user1").totpEnabled(true).build();
        when(userRepository.findByUsernameOrEmail("user1", "user1")).thenReturn(Optional.of(user));
        when(request.getSession()).thenReturn(session);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(verificationService).generateAndSendOtp(user, "LOGIN_2FA");
        verify(session).setAttribute("pending2faUserId", 1L);
        verify(response).sendRedirect("/auth/2fa-login");
    }
}
