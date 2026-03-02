package main.test.mapper;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.User;
import com.rev.app.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userMapper = new UserMapper(passwordEncoder);
    }

    @Test
    void testToEntity() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@mail.com");
        dto.setMasterPassword("plain");
        
        when(passwordEncoder.encode("plain")).thenReturn("encoded");

        User user = userMapper.toEntity(dto);
        
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("encoded", user.getMasterPasswordHash());
        assertTrue(user.isEmailVerified());
    }

    @Test
    void testToEntity_Null() {
        assertNull(userMapper.toEntity(null));
    }
}
