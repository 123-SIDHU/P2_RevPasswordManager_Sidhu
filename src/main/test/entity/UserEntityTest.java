package main.test.entity;

import com.rev.app.entity.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void testUserBuilder() {
        User user = User.builder()
                .username("test")
                .email("test@mail.com")
                .emailVerified(true)
                .vaultEntries(new ArrayList<>())
                .build();

        assertEquals("test", user.getUsername());
        assertTrue(user.isEmailVerified());
        assertNotNull(user.getVaultEntries());
    }

    @Test
    void testUserNoArgsConstructor() {
        User user = new User();
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
    }
}
