package main.test.repository;

import com.rev.app.entity.SecurityQuestion;
import com.rev.app.entity.User;
import com.rev.app.repository.ISecurityQuestionRepository;
import com.rev.app.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("h2")
class ISecurityQuestionRepositoryTest {

    @Autowired
    private ISecurityQuestionRepository repository;
    @Autowired
    private IUserRepository userRepository;

    @Test
    void testFindByUserId() {
        User user = User.builder().username("sq").email("sq@mail.com").masterPasswordHash("h").build();
        user = userRepository.save(user);

        SecurityQuestion sq = SecurityQuestion.builder()
                .user(user)
                .questionText("Wh?")
                .answerHash("a")
                .build();
        repository.save(sq);

        List<SecurityQuestion> result = repository.findByUserId(user.getId());
        assertEquals(1, result.size());
        assertEquals("Wh?", result.get(0).getQuestionText());
    }
}
