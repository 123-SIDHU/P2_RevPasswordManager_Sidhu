package com.rev.app.mapper;

import com.rev.app.dto.VaultEntryDTO;
import com.rev.app.entity.User;
import com.rev.app.entity.VaultEntry;
import com.rev.app.service.IEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaultEntryMapperTest {

    @Mock
    private IEncryptionService encryptionService;

    private VaultEntryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VaultEntryMapper(encryptionService);
    }

    @Test
    void testToEntity_Success() {
        VaultEntryDTO dto = new VaultEntryDTO();
        dto.setAccountName("Acc");
        dto.setPassword("raw");
        dto.setCategory(VaultEntry.Category.SOCIAL_MEDIA);

        User user = new User();
        when(encryptionService.encrypt("raw")).thenReturn("enc");

        VaultEntry entry = mapper.toEntity(dto, user);

        assertNotNull(entry);
        assertEquals("Acc", entry.getAccountName());
        assertEquals("enc", entry.getEncryptedPassword());
        assertEquals(VaultEntry.Category.SOCIAL_MEDIA, entry.getCategory());
    }

    @Test
    void testToDto_Success() {
        VaultEntry entry = VaultEntry.builder()
                .id(1L)
                .accountName("Acc")
                .createdAt(LocalDateTime.now())
                .build();

        VaultEntryDTO dto = mapper.toDto(entry);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("••••••••", dto.getPassword());
    }
}
