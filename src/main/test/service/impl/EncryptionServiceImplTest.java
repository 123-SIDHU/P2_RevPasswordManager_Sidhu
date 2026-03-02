package main.test.service.impl;

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
        ReflectionTestUtils.setField(encryptionService, "secret", "my-super-secret-key");
    }

    @Test
    void testEncryptDecrypt() {
        String original = "Hello World";
        String encrypted = encryptionService.encrypt(original);
        assertNotEquals(original, encrypted);
        
        String decrypted = encryptionService.decrypt(encrypted);
        assertEquals(original, decrypted);
    }

    @Test
    void testEncrypt_DifferentInputs() {
        String enc1 = encryptionService.encrypt("input1");
        String enc2 = encryptionService.encrypt("input2");
        assertNotEquals(enc1, enc2);
    }
}
