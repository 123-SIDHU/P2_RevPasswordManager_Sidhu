package com.rev.app.mapper;

import com.rev.app.dto.SecurityQuestionDTO;
import com.rev.app.entity.SecurityQuestion;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityQuestionMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private SecurityQuestionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SecurityQuestionMapper(passwordEncoder);
    }

    @Test
    void testToEntity_Success() {
        SecurityQuestionDTO dto = new SecurityQuestionDTO();
        dto.setQuestionText("What?");
        dto.setAnswer(" Ans ");

        User user = new User();
        when(passwordEncoder.encode("ans")).thenReturn("hashed_ans");

        SecurityQuestion entity = mapper.toEntity(dto, user);

        assertNotNull(entity);
        assertEquals("What?", entity.getQuestionText());
        assertEquals("hashed_ans", entity.getAnswerHash());
        assertEquals(user, entity.getUser());
    }
}
