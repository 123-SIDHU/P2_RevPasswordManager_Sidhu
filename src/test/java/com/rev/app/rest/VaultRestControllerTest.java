package com.rev.app.rest;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaultRestControllerTest {

    @Mock
    private IVaultService vaultService;
    @Mock
    private IUserService userService;
    @Mock
    private AuthUtil authUtil;

    private VaultRestController controller;

    @BeforeEach
    void setUp() {
        controller = new VaultRestController(vaultService, userService, authUtil);
    }

    @Test
    void testGetAllEntries() {
        User user = new User();
        user.setId(1L);
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(vaultService.getAllEntries(eq(1L), any(), any(), any())).thenReturn(Collections.singletonList(new VaultEntryDTO()));

        ResponseEntity<List<VaultEntryDTO>> response = controller.getAllEntries(null, null, "name");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testAddEntry_Success() {
        User user = new User();
        when(authUtil.getCurrentUser()).thenReturn(user);

        ResponseEntity<Map<String, String>> response = controller.addEntry(new VaultEntryDTO());
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(vaultService).addEntry(eq(user), any());
    }

    @Test
    void testDeleteEntry() {
        User user = new User();
        user.setId(1L);
        when(authUtil.getCurrentUser()).thenReturn(user);

        ResponseEntity<Void> response = controller.deleteEntry(100L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vaultService).deleteEntry(1L, 100L);
    }
}
