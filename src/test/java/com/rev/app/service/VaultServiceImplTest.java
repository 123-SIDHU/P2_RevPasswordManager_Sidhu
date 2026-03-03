package com.rev.app.service;

import com.rev.app.dto.VaultEntryDTO;
import com.rev.app.entity.User;
import com.rev.app.entity.VaultEntry;
import com.rev.app.mapper.VaultEntryMapper;
import com.rev.app.repository.IVaultEntryRepository;
import com.rev.app.service.IEncryptionService;
import com.rev.app.service.impl.VaultServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaultServiceImplTest {

    @Mock
    private IVaultEntryRepository vaultRepo;
    @Mock
    private IEncryptionService encryptionService;
    @Mock
    private VaultEntryMapper vaultEntryMapper;

    private VaultServiceImpl vaultService;

    @BeforeEach
    void setUp() {
        vaultService = new VaultServiceImpl(vaultRepo, encryptionService, vaultEntryMapper);
    }

    @Test
    void testAddEntry() {
        User user = new User();
        VaultEntryDTO dto = new VaultEntryDTO();
        VaultEntry entry = new VaultEntry();

        when(vaultEntryMapper.toEntity(dto, user)).thenReturn(entry);
        when(vaultRepo.save(entry)).thenReturn(entry);

        VaultEntry saved = vaultService.addEntry(user, dto);

        assertNotNull(saved);
        verify(vaultRepo).save(entry);
    }

    @Test
    void testDeleteEntry() {
        VaultEntry entry = new VaultEntry();
        when(vaultRepo.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(entry));

        vaultService.deleteEntry(1L, 1L);

        verify(vaultRepo).delete(entry);
    }
}
