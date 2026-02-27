package com.rev.app.config;

import com.rev.app.entity.User;
import com.rev.app.repository.IUserRepository;
import com.rev.app.service.VerificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final IUserRepository IUserRepository;
    private final VerificationService verificationService;

    public CustomAuthenticationSuccessHandler(IUserRepository IUserRepository, VerificationService verificationService) {
        this.IUserRepository = IUserRepository;
        this.verificationService = verificationService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Get authenticated user
        String username = authentication.getName();
        User user = IUserRepository.findByUsernameOrEmail(username, username).orElse(null);

        if (user != null && user.isTotpEnabled()) {
            // Send 2FA email
            verificationService.generateAndSendOtp(user, "LOGIN_2FA");

            // Clear the security context because we don't want to fully log them in yet
            SecurityContextHolder.clearContext();

            // Store user id in session to verify 2FA
            request.getSession().setAttribute("pending2faUserId", user.getId());

            // Redirect to 2FA page
            response.sendRedirect("/auth/2fa-login");
            return;
        }

        // If no 2FA, proceed to dashboard
        response.sendRedirect("/dashboard");
    }
}
