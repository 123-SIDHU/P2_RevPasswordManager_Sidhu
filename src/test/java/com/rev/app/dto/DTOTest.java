package com.rev.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DTOTest {

    @Test
    void testLoginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setUsernameOrEmail("user");
        assertEquals("user", dto.getUsernameOrEmail());
    }

    @Test
    void testRegisterDTO() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("test@mail.com");
        assertEquals("test@mail.com", dto.getEmail());
    }

    @Test
    void testVaultEntryDTO() {
        VaultEntryDTO dto = new VaultEntryDTO();
        dto.setAccountName("Acc");
        assertEquals("Acc", dto.getAccountName());
    }

    @Test
    void testProfileUpdateDTO() {
        ProfileUpdateDTO dto = new ProfileUpdateDTO();
        dto.setFullName("Full Name");
        assertEquals("Full Name", dto.getFullName());
    }

    @Test
    void testPasswordGeneratorConfigDTO() {
        PasswordGeneratorConfigDTO dto = new PasswordGeneratorConfigDTO();
        dto.setLength(16);
        assertEquals(16, dto.getLength());
    }

    @Test
    void testPasswordResultDTO() {
        PasswordResultDTO dto = new PasswordResultDTO("pass", 4, "Strong");
        assertEquals("pass", dto.getPassword());
        assertEquals("Strong", dto.getLabel());
    }

    @Test
    void testSecurityQuestionDTO() {
        SecurityQuestionDTO dto = new SecurityQuestionDTO();
        dto.setQuestionText("Q");
        assertEquals("Q", dto.getQuestionText());
    }

    @Test
    void testChangePasswordDTO() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setNewPassword("new");
        assertEquals("new", dto.getNewPassword());
    }
}
