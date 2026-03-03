package com.rev.app.service;

import com.rev.app.dto.PasswordGeneratorConfigDTO;
import com.rev.app.service.impl.PasswordGeneratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorServiceImplTest {

    private PasswordGeneratorServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PasswordGeneratorServiceImpl();
    }

    @Test
    void testGenerate() {
        PasswordGeneratorConfigDTO config = new PasswordGeneratorConfigDTO();
        config.setLength(12);
        config.setCount(2);
        config.setIncludeUppercase(true);
        config.setIncludeLowercase(true);
        config.setIncludeNumbers(true);

        List<String> results = service.generate(config);

        assertEquals(2, results.size());
        assertEquals(12, results.get(0).length());
    }

    @Test
    void testStrengthScore() {
        assertTrue(service.strengthScore("weak") <= 1);
        assertTrue(service.strengthScore("Strong123!") >= 3);
    }
}
