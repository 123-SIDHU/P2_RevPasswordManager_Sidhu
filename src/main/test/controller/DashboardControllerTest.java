package main.test.controller;

import com.rev.app.controller.DashboardController;
import com.rev.app.entity.User;
import com.rev.app.service.ISecurityAuditService;
import com.rev.app.service.IVaultService;
import com.rev.app.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardControllerTest {

    @Mock
    private IVaultService vaultService;
    @Mock
    private ISecurityAuditService auditService;
    @Mock
    private AuthUtil authUtil;
    @Mock
    private Model model;

    private DashboardController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new DashboardController(vaultService, auditService, authUtil);
    }

    @Test
    void testDashboard_Success() {
        User user = User.builder().id(1L).username("testuser").build();
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(vaultService.countByUser(1L)).thenReturn(10L);
        
        ISecurityAuditService.AuditReport report = new ISecurityAuditService.AuditReport();
        // Lists are already initialized in AuditReport
        
        when(auditService.generateReport(eq(1L), anyInt())).thenReturn(report);
        when(vaultService.getRecentEntries(eq(1L), anyInt())).thenReturn(new ArrayList<>());

        String view = controller.dashboard(model);
        
        assertEquals("dashboard", view);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("totalPasswords", 10L);
    }

    @Test
    void testDashboard_NoUser() {
        when(authUtil.getCurrentUser()).thenReturn(null);
        assertEquals("redirect:/login", controller.dashboard(model));
    }
}
