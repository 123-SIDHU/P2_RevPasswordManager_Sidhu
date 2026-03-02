package com.rev.app.controller;

import com.rev.app.dto.PasswordGeneratorConfigDTO;
import com.rev.app.entity.User;
import com.rev.app.service.IPasswordGeneratorService;
import com.rev.app.service.IUserService;
import com.rev.app.service.IVaultService;
import com.rev.app.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordGeneratorControllerTest {

    @Mock
    private IPasswordGeneratorService generatorService;
    @Mock
    private IVaultService vaultService;
    @Mock
    private IUserService userService;
    @Mock
    private AuthUtil authUtil;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;

    private PasswordGeneratorController controller;

    @BeforeEach
    void setUp() {
        controller = new PasswordGeneratorController(generatorService, vaultService, userService, authUtil);
    }

    @Test
    void testGeneratorPage() {
        assertEquals("generator", controller.generatorPage(model));
        verify(model).addAttribute(eq("config"), any(PasswordGeneratorConfigDTO.class));
    }

    @Test
    void testGenerate() {
        PasswordGeneratorConfigDTO config = new PasswordGeneratorConfigDTO();
        when(generatorService.generate(config)).thenReturn(Collections.singletonList("Pass123!"));
        when(generatorService.strengthScore("Pass123!")).thenReturn(4);
        when(generatorService.strengthLabel(4)).thenReturn("Strong");

        String view = controller.generate(config, model);
        
        assertEquals("generator", view);
        verify(model).addAttribute(eq("results"), anyList());
    }

    @Test
    void testSaveToVault_Success() {
        User user = new User();
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(userService.verifyMasterPassword(user, "master")).thenReturn(true);

        String view = controller.saveToVault("pw", "acc", "master", redirectAttributes);
        
        assertEquals("redirect:/vault", view);
        verify(vaultService).addEntry(eq(user), any());
    }
}
