package com.rev.app.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.rev.app.entity.User;
import com.rev.app.entity.VaultEntry;

@DataJpaTest
class IVaultEntryRepositoryTest {

    @Autowired
    private IVaultEntryRepository repository;
    @Autowired
    private IUserRepository userRepository;

    @Test
    void testFindByUserId() {
        User user = User.builder().username("v").email("v@mail.com").masterPasswordHash("h").build();
        user = userRepository.save(user);

        VaultEntry ve = VaultEntry.builder()
                .user(user)
                .accountName("Acc")
                .encryptedPassword("hash")
                .build();
        repository.save(ve);

        List<VaultEntry> result = repository.findByUserIdOrderByAccountNameAsc(user.getId());
        assertEquals(1, result.size());
        assertEquals("Acc", result.get(0).getAccountName());
    }

    @Test
    void testSearch() {
        User user = User.builder().username("v2").email("v2@mail.com").masterPasswordHash("h").build();
        user = userRepository.save(user);

        VaultEntry ve = VaultEntry.builder()
                .user(user)
                .accountName("Target")
                .encryptedPassword("hash")
                .build();
        repository.save(ve);

        List<VaultEntry> result = repository.search(user.getId(), "tar");
        assertEquals(1, result.size());
    }
}
