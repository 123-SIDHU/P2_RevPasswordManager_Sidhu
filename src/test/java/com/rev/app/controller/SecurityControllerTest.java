package com.rev.app.controller;

import com.rev.app.dto.ChangePasswordDTO;
import com.rev.app.entity.User;
import com.rev.app.repository.ISecurityQuestionRepository;
import com.rev.app.service.ISecurityAuditService;
import com.rev.app.service.IUserService;
import com.rev.app.service.IVerificationService;
import com.rev.app.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityControllerTest {

    @Mock
    private IUserService userService;
    @Mock
    private ISecurityAuditService auditService;
    @Mock
    private IVerificationService verificationService;
    @Mock
    private ISecurityQuestionRepository sqRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthUtil authUtil;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private BindingResult bindingResult;

    private SecurityController controller;

    @BeforeEach
    void setUp() {
        controller = new SecurityController(userService, auditService, verificationService, sqRepository, passwordEncoder, authUtil);
    }

    @Test
    void testAudit() {
        User user = User.builder().id(1L).build();
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(auditService.generateReport(eq(1L), anyInt())).thenReturn(new ISecurityAuditService.AuditReport());

        assertEquals("security/audit", controller.audit(model));
        verify(model).addAttribute(eq("report"), any());
    }

    @Test
    void testChangePassword_Success() {
        User user = User.builder().id(1L).build();
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(false);

        assertEquals("redirect:/dashboard", controller.changePassword(new ChangePasswordDTO(), bindingResult, redirectAttributes, model));
        verify(userService).changeMasterPassword(eq(1L), any());
    }

    @Test
    void testEnable2FA() {
        User user = User.builder().email("t@m.com").build();
        when(authUtil.getCurrentUser()).thenReturn(user);

        assertEquals("redirect:/security/2fa/verify", controller.enable2FA(redirectAttributes));
        verify(verificationService).generateAndSendOtp(user, "2FA");
    }
}
