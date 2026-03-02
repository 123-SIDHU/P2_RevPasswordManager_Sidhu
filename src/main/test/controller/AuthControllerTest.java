package main.test.controller;

import com.rev.app.controller.AuthController;
import com.rev.app.dto.RegisterDTO;
import com.rev.app.service.IPasswordRecoveryService;
import com.rev.app.service.IUserService;
import com.rev.app.service.IVerificationService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private IUserService userService;
    @Mock
    private IPasswordRecoveryService recoveryService;
    @Mock
    private IVerificationService verificationService;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private HttpSession session;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AuthController(userService, recoveryService, verificationService);
    }

    @Test
    void testLoginPage() {
        assertEquals("auth/login", controller.loginPage(null, null, null, model));
        verify(model, never()).addAttribute(anyString(), anyString());
        
        assertEquals("auth/login", controller.loginPage("error", "logout", "expired", model));
        verify(model, times(3)).addAttribute(anyString(), anyString());
    }

    @Test
    void testRegisterPage() {
        assertEquals("auth/register", controller.registerPage(model));
        verify(model).addAttribute(eq("registerDTO"), any(RegisterDTO.class));
    }

    @Test
    void testDoRegister_Success() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("test@mail.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(verificationService.sendRegistrationOtp("test@mail.com")).thenReturn("123456");

        String view = controller.doRegister(dto, bindingResult, redirectAttributes, session, model);
        
        assertEquals("redirect:/auth/verify-email", view);
        verify(session).setAttribute(eq("pendingRegisterOtp"), eq("123456"));
    }

    @Test
    void testDoRegister_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        assertEquals("auth/register", controller.doRegister(new RegisterDTO(), bindingResult, redirectAttributes, session, model));
    }

    @Test
    void testVerifyEmailPage() {
        when(session.getAttribute("pendingRegisterDTO")).thenReturn(new RegisterDTO());
        assertEquals("auth/verify-email", controller.verifyEmailPage(session, model));
    }

    @Test
    void testDoVerifyEmail_Success() {
        RegisterDTO dto = new RegisterDTO();
        when(session.getAttribute("pendingRegisterDTO")).thenReturn(dto);
        when(session.getAttribute("pendingRegisterOtp")).thenReturn("123456");
        when(session.getAttribute("pendingRegisterOtpExpiry")).thenReturn(System.currentTimeMillis() + 100000);

        assertEquals("redirect:/login", controller.doVerifyEmail("123456", session, redirectAttributes));
        verify(userService).register(dto);
    }

    @Test
    void testShowQuestions() {
        when(recoveryService.getQuestions("user")).thenReturn(new ArrayList<>());
        assertEquals("auth/recover-questions", controller.showQuestions("user", model, redirectAttributes));
    }

    @Test
    void testResetPassword_Success() {
        assertEquals("redirect:/login", controller.resetPassword("user", "pass1234", "pass1234", redirectAttributes));
        verify(recoveryService).resetPassword("user", "pass1234");
    }
}
