package main.test.mapper;

import com.rev.app.dto.VaultEntryDTO;
import com.rev.app.entity.User;
import com.rev.app.entity.VaultEntry;
import com.rev.app.mapper.VaultEntryMapper;
import com.rev.app.service.IEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class VaultEntryMapperTest {

    @Mock
    private IEncryptionService encryptionService;

    private VaultEntryMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new VaultEntryMapper(encryptionService);
    }

    @Test
    void testToEntity() {
        VaultEntryDTO dto = new VaultEntryDTO();
        dto.setAccountName("Test");
        dto.setPassword("plain");
        
        when(encryptionService.encrypt("plain")).thenReturn("enc");

        VaultEntry entity = mapper.toEntity(dto, new User());
        
        assertNotNull(entity);
        assertEquals("Test", entity.getAccountName());
        assertEquals("enc", entity.getEncryptedPassword());
    }

    @Test
    void testToDto() {
        VaultEntry entity = new VaultEntry();
        entity.setAccountName("Test");
        entity.setCreatedAt(LocalDateTime.now());
        
        VaultEntryDTO dto = mapper.toDto(entity);
        
        assertNotNull(dto);
        assertEquals("Test", dto.getAccountName());
        assertEquals("••••••••", dto.getPassword());
    }

    @Test
    void testToDecryptedDto() {
        VaultEntry entity = new VaultEntry();
        entity.setEncryptedPassword("enc");
        
        when(encryptionService.decrypt("enc")).thenReturn("plain");

        VaultEntryDTO dto = mapper.toDecryptedDto(entity);
        
        assertNotNull(dto);
        assertEquals("plain", dto.getPassword());
    }
}
