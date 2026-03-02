package com.rev.app.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void testVaultEntry() {
        VaultEntry entry = VaultEntry.builder()
                .id(1L)
                .accountName("Acc")
                .encryptedPassword("hash")
                .favorite(true)
                .build();

        assertEquals(1L, entry.getId());
        assertEquals("Acc", entry.getAccountName());
        assertTrue(entry.isFavorite());
    }

    @Test
    void testSecurityQuestion() {
        User user = new User();
        SecurityQuestion sq = SecurityQuestion.builder()
                .questionText("What?")
                .answerHash("hash")
                .user(user)
                .build();

        assertEquals("What?", sq.getQuestionText());
        assertEquals(user, sq.getUser());
    }

    @Test
    void testVerificationCode() {
        VerificationCode vc = VerificationCode.builder()
                .code("123456")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        assertEquals("123456", vc.getCode());
        assertNotNull(vc.getExpiresAt());
    }
}
