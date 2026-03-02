package main.test.mapper;

import com.rev.app.dto.SecurityQuestionDTO;
import com.rev.app.entity.SecurityQuestion;
import com.rev.app.entity.User;
import com.rev.app.mapper.SecurityQuestionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class SecurityQuestionMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private SecurityQuestionMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new SecurityQuestionMapper(passwordEncoder);
    }

    @Test
    void testToEntity() {
        SecurityQuestionDTO dto = new SecurityQuestionDTO();
        dto.setQuestionText("What?");
        dto.setAnswer(" Ans ");
        
        User user = new User();
        when(passwordEncoder.encode("ans")).thenReturn("hash");

        SecurityQuestion entity = mapper.toEntity(dto, user);
        
        assertNotNull(entity);
        assertEquals("What?", entity.getQuestionText());
        assertEquals("hash", entity.getAnswerHash());
        assertEquals(user, entity.getUser());
    }
}
