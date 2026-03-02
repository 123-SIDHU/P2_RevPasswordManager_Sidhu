package main.test.service.impl;

import com.rev.app.entity.VaultEntry;
import com.rev.app.repository.IVaultEntryRepository;
import com.rev.app.service.IEncryptionService;
import com.rev.app.service.IPasswordGeneratorService;
import com.rev.app.service.ISecurityAuditService;
import com.rev.app.service.impl.SecurityAuditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class SecurityAuditServiceImplTest {

    @Mock
    private IVaultEntryRepository vaultRepo;
    @Mock
    private IEncryptionService encryptionService;
    @Mock
    private IPasswordGeneratorService generatorService;

    private SecurityAuditServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SecurityAuditServiceImpl(vaultRepo, encryptionService, generatorService);
    }

    @Test
    void testGenerateReport_Empty() {
        when(vaultRepo.findByUserIdOrderByAccountNameAsc(1L)).thenReturn(Collections.emptyList());
        
        ISecurityAuditService.AuditReport report = service.generateReport(1L, 90);
        
        assertEquals(100, report.getSecurityScore());
        assertEquals(0, report.getTotalEntries());
    }

    @Test
    void testGenerateReport_WeakPassword() {
        VaultEntry entry = VaultEntry.builder()
                .id(1L)
                .accountName("Weak")
                .encryptedPassword("enc")
                .createdAt(LocalDateTime.now())
                .build();
        
        when(vaultRepo.findByUserIdOrderByAccountNameAsc(1L)).thenReturn(Collections.singletonList(entry));
        when(encryptionService.decrypt("enc")).thenReturn("123");
        when(generatorService.strengthScore("123")).thenReturn(1);
        when(generatorService.strengthLabel(1)).thenReturn("Weak");

        ISecurityAuditService.AuditReport report = service.generateReport(1L, 90);
        
        assertEquals(1, report.getWeakPasswords().size());
        assertTrue(report.getSecurityScore() < 100);
    }
}
