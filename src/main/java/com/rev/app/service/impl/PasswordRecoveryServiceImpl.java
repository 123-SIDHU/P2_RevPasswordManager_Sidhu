package com.rev.app.service.impl;

import com.rev.app.entity.SecurityQuestion;
import com.rev.app.entity.User;
import com.rev.app.exception.InvalidCredentialsException;
import com.rev.app.exception.ValidationException;
import com.rev.app.repository.ISecurityQuestionRepository;
import com.rev.app.repository.IUserRepository;
import com.rev.app.service.IPasswordRecoveryService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PasswordRecoveryServiceImpl implements IPasswordRecoveryService {

    private static final Logger logger = LogManager.getLogger(PasswordRecoveryServiceImpl.class);

    private final IUserRepository IUserRepository;
    private final ISecurityQuestionRepository sqRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordRecoveryServiceImpl(IUserRepository IUserRepository,
            ISecurityQuestionRepository sqRepository,
            PasswordEncoder passwordEncoder) {
        this.IUserRepository = IUserRepository;
        this.sqRepository = sqRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<SecurityQuestion> getQuestions(String usernameOrEmail) {
        User user = IUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Account not found"));
        List<SecurityQuestion> questions = sqRepository.findByUserId(user.getId());
        if (questions.isEmpty()) {
            throw new ValidationException("No security questions configured for this account");
        }
        return questions;
    }

    @Override
    public boolean validateAnswers(Long userId, List<String> answers) {
        List<SecurityQuestion> questions = sqRepository.findByUserId(userId);
        if (answers.size() != questions.size())
            return false;
        for (int i = 0; i < questions.size(); i++) {
            String answer = answers.get(i) == null ? "" : answers.get(i).toLowerCase().trim();
            if (!passwordEncoder.matches(answer, questions.get(i).getAnswerHash())) {
                logger.warn("Security answer mismatch for user {} question idx={}", userId, i);
                return false;
            }
        }
        return true;
    }

    @Override
    public void resetPassword(String usernameOrEmail, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new ValidationException("New password must be at least 8 characters");
        }
        User user = IUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Account not found"));
        user.setMasterPasswordHash(passwordEncoder.encode(newPassword));
        IUserRepository.save(user);
        logger.info("Password reset via security questions for user: {}", user.getUsername());
    }
}
