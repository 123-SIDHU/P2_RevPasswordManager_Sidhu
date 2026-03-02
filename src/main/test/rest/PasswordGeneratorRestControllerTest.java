package main.test.rest;

import com.rev.app.dto.PasswordGeneratorConfigDTO;
import com.rev.app.dto.PasswordResultDTO;
import com.rev.app.rest.PasswordGeneratorRestController;
import com.rev.app.service.IPasswordGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PasswordGeneratorRestControllerTest {

    @Mock
    private IPasswordGeneratorService generatorService;

    private PasswordGeneratorRestController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PasswordGeneratorRestController(generatorService);
    }

    @Test
    void testGeneratePasswords() {
        PasswordGeneratorConfigDTO config = new PasswordGeneratorConfigDTO();
        config.setLength(12);
        config.setCount(1);
        
        when(generatorService.generate(any())).thenReturn(Collections.singletonList("Pass123!"));
        when(generatorService.strengthScore("Pass123!")).thenReturn(4);
        when(generatorService.strengthLabel(4)).thenReturn("Very Strong");

        ResponseEntity<List<PasswordResultDTO>> response = controller.generatePasswords(config);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Very Strong", response.getBody().get(0).getLabel());
    }
}
