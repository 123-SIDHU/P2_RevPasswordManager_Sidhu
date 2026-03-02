package main.test.service.impl;

import com.rev.app.dto.VaultEntryDTO;
import com.rev.app.entity.User;
import com.rev.app.entity.VaultEntry;
import com.rev.app.mapper.VaultEntryMapper;
import com.rev.app.repository.IVaultEntryRepository;
import com.rev.app.service.IEncryptionService;
import com.rev.app.service.impl.VaultServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VaultServiceImplTest {

    @Mock
    private IVaultEntryRepository vaultRepo;
    @Mock
    private IEncryptionService encryptionService;
    @Mock
    private VaultEntryMapper vaultEntryMapper;

    private VaultServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new VaultServiceImpl(vaultRepo, encryptionService, vaultEntryMapper);
    }

    @Test
    void testAddEntry() {
        User user = new User();
        VaultEntryDTO dto = new VaultEntryDTO();
        VaultEntry entry = new VaultEntry();
        
        when(vaultEntryMapper.toEntity(dto, user)).thenReturn(entry);
        when(vaultRepo.save(entry)).thenReturn(entry);

        VaultEntry result = service.addEntry(user, dto);
        assertNotNull(result);
        verify(vaultRepo).save(entry);
    }

    @Test
    void testDeleteEntry() {
        VaultEntry entry = new VaultEntry();
        when(vaultRepo.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(entry));

        service.deleteEntry(1L, 1L);
        verify(vaultRepo).delete(entry);
    }

    @Test
    void testToggleFavorite() {
        VaultEntry entry = new VaultEntry();
        entry.setFavorite(false);
        when(vaultRepo.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(entry));

        service.toggleFavorite(1L, 1L);
        assertTrue(entry.isFavorite());
        verify(vaultRepo).save(entry);
    }
}
