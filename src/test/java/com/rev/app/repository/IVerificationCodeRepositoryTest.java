package com.rev.app.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.rev.app.entity.User;
import com.rev.app.entity.VerificationCode;

@DataJpaTest
class IVerificationCodeRepositoryTest {

    @Autowired
    private IVerificationCodeRepository repository;
    @Autowired
    private IUserRepository userRepository;

    @Test
    void testFindByUserAndCodeAndPurpose() {
        User user = User.builder().username("vcode").email("vc@mail.com").masterPasswordHash("h").build();
        user = userRepository.save(user);

        VerificationCode code = VerificationCode.builder()
                .user(user)
                .code("123456")
                .purpose("TEST")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        repository.save(code);

        Optional<VerificationCode> result = repository.findTopByUserIdAndPurposeAndUsedFalseOrderByCreatedAtDesc(user.getId(), "TEST");
        assertTrue(result.isPresent());
        assertEquals("123456", result.get().getCode());
    }
}
