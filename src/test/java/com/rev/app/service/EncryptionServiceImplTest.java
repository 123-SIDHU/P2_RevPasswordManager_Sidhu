package com.rev.app.service;

import com.rev.app.service.impl.EncryptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EncryptionServiceImplTest {

    private EncryptionServiceImpl encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionServiceImpl();
        ReflectionTestUtils.setField(encryptionService, "secret", "mySuperSecretKey");
    }

    @Test
    void testEncryptDecrypt() {
        String plain = "SecretMessage123";
        String encrypted = encryptionService.encrypt(plain);
        assertNotEquals(plain, encrypted);

        String decrypted = encryptionService.decrypt(encrypted);
        assertEquals(plain, decrypted);
    }
}
