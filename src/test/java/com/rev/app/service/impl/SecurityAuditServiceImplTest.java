package com.rev.app.service.impl;

import com.rev.app.entity.VaultEntry;
import com.rev.app.repository.IVaultEntryRepository;
import com.rev.app.service.IEncryptionService;
import com.rev.app.service.IPasswordGeneratorService;
import com.rev.app.service.ISecurityAuditService.AuditReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityAuditServiceImplTest {

    @Mock
    private IVaultEntryRepository vaultRepo;
    @Mock
    private IEncryptionService encryptionService;
    @Mock
    private IPasswordGeneratorService generatorService;

    private SecurityAuditServiceImpl auditService;

    @BeforeEach
    void setUp() {
        auditService = new SecurityAuditServiceImpl(vaultRepo, encryptionService, generatorService);
    }

    @Test
    void testGenerateReport_Empty() {
        when(vaultRepo.findByUserIdOrderByAccountNameAsc(anyLong())).thenReturn(Collections.emptyList());
        
        AuditReport report = auditService.generateReport(1L, 90);
        
        assertEquals(100, report.getSecurityScore());
        assertEquals(0, report.getTotalEntries());
    }

    @Test
    void testGenerateReport_WithIssues() {
        VaultEntry entry = VaultEntry.builder().id(1L).accountName("Test").encryptedPassword("enc").build();
        when(vaultRepo.findByUserIdOrderByAccountNameAsc(anyLong())).thenReturn(List.of(entry));
        when(encryptionService.decrypt("enc")).thenReturn("weak");
        when(generatorService.strengthScore("weak")).thenReturn(1);
        when(generatorService.strengthLabel(1)).thenReturn("Weak");

        AuditReport report = auditService.generateReport(1L, 90);

        assertTrue(report.getWeakPasswords().size() > 0);
        assertTrue(report.getSecurityScore() < 100);
    }
}
