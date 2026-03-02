package main.test.service.impl;

import com.rev.app.dto.PasswordGeneratorConfigDTO;
import com.rev.app.service.impl.PasswordGeneratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        config.setCount(5);
        config.setIncludeUppercase(true);
        config.setIncludeNumbers(true);

        List<String> results = service.generate(config);
        
        assertEquals(5, results.size());
        for (String pw : results) {
            assertEquals(12, pw.length());
        }
    }

    @ParameterizedTest
    @CsvSource({
        "'short', 0",
        "'password8', 2",
        "'Password8', 2",
        "'P@ssw0rd123!', 4"
    })
    void testStrengthScore(String password, int expectedScore) {
        assertEquals(expectedScore, service.strengthScore(password), "Score for " + password + " mismatch");
    }

    @Test
    void testStrengthLabel() {
        assertEquals("Weak", service.strengthLabel(1));
        assertEquals("Medium", service.strengthLabel(2));
        assertEquals("Strong", service.strengthLabel(3));
        assertEquals("Very Strong", service.strengthLabel(4));
    }
}
