package com.rev.app.controller;

import com.rev.app.dto.VaultEntryDTO;
import com.rev.app.entity.User;
import com.rev.app.service.IUserService;
import com.rev.app.service.IVaultService;
import com.rev.app.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaultControllerTest {

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
    @Mock
    private BindingResult bindingResult;

    private VaultController controller;

    @BeforeEach
    void setUp() {
        controller = new VaultController(vaultService, userService, authUtil);
    }

    @Test
    void testVaultList() {
        User user = User.builder().id(1L).build();
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(vaultService.getAllEntries(eq(1L), any(), any(), any())).thenReturn(new ArrayList<>());

        assertEquals("vault/vault", controller.vaultList(null, null, "name", "list", model));
        verify(model).addAttribute(eq("entries"), anyList());
    }

    @Test
    void testAddEntry_Success() {
        User user = User.builder().id(1L).build();
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.verifyMasterPassword(user, "master")).thenReturn(true);

        assertEquals("redirect:/vault", controller.addEntry(new VaultEntryDTO(), bindingResult, "master", model, redirectAttributes));
    }

    @Test
    void testDeleteEntry_Success() {
        User user = User.builder().id(1L).build();
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(userService.verifyMasterPassword(user, "master")).thenReturn(true);

        assertEquals("redirect:/vault", controller.deleteEntry(100L, "master", redirectAttributes));
        verify(vaultService).deleteEntry(1L, 100L);
    }
}
