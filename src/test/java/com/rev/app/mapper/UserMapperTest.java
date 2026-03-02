package com.rev.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.User;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper(passwordEncoder);
    }

    @Test
    void testToEntity_Success() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("test");
        dto.setEmail("test@mail.com");
        dto.setMasterPassword("pass");

        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("test", user.getUsername());
        assertEquals("hashed", user.getMasterPasswordHash());
        assertTrue(user.isEmailVerified());
    }

    @Test
    void testToEntity_Null() {
        assertNull(userMapper.toEntity(null));
    }
}
