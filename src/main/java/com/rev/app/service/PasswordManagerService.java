package com.rev.app.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rev.app.entity.Credential;
import com.rev.app.entity.SecurityQuestion;
import com.rev.app.entity.User;
import com.rev.app.repository.CredentialRepository;
import com.rev.app.repository.SecurityQuestionRepository;
import com.rev.app.repository.UserRepository;
import com.rev.app.util.CryptoUtil;

@Service
@Transactional
public class PasswordManagerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;


    public User registerUser(User user, List<SecurityQuestion> questions) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String hash = CryptoUtil.hashPassword(user.getMasterPasswordHash());
        user.setMasterPasswordHash(hash);

        User savedUser = userRepository.save(user);

        for (SecurityQuestion q : questions) {
            q.setUserId(savedUser.getId());
            q.setAnswerHash(CryptoUtil.hashPassword(q.getAnswerHash()));
            securityQuestionRepository.save(q);
        }
        return savedUser;
    }

    public User login(String username, String masterPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (CryptoUtil.verifyPassword(masterPassword, user.getMasterPasswordHash())) {
                return user;
            }
        }
        return null;
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<Credential> getCredentials(int userId) {
        return credentialRepository.findByUserId(userId);
    }

    public List<Credential> searchCredentials(int userId, String keyword) {
        return credentialRepository.searchCredentials(userId, keyword);
    }

    public void addCredential(Credential credential, String masterPassword) {
        if (credential.getEncryptedPassword() != null) {
            byte[] key = CryptoUtil.deriveKey(masterPassword);
            String encrypted = CryptoUtil.encrypt(credential.getEncryptedPassword(), key);
            credential.setEncryptedPassword(encrypted);
        }
        credentialRepository.save(credential);
    }

    public Credential getCredential(int credentialId, String masterPassword) {
        Credential cred = credentialRepository.findById(credentialId).orElseThrow(() -> new RuntimeException("Not found"));
        // Decrypt
        byte[] key = CryptoUtil.deriveKey(masterPassword);
        try {
            String decrypted = CryptoUtil.decrypt(cred.getEncryptedPassword(), key);
            // Return a transient copy with decrypted password, do NOT save this back to DB
            Credential copy = new Credential(cred.getId(), cred.getUserId(), cred.getAccountName(),
                    cred.getUsername(), decrypted, cred.getUrl(), cred.getNotes(), cred.getCategory(), cred.isFavorite());
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Invalid Master Password or Decryption Failed");
        }
    }

    public void updateCredential(Credential credential, String masterPassword) {
        if (credential.getEncryptedPassword() != null && !credential.getEncryptedPassword().isEmpty()) {
            // Re-encrypt
            byte[] key = CryptoUtil.deriveKey(masterPassword);
            String encrypted = CryptoUtil.encrypt(credential.getEncryptedPassword(), key);
            credential.setEncryptedPassword(encrypted);
        } else {
            // Preserve old password if not provided
            Credential old = credentialRepository.findById(credential.getId()).orElseThrow();
            credential.setEncryptedPassword(old.getEncryptedPassword());
        }
        credentialRepository.save(credential);
    }

    public void deleteCredential(int id) {
        credentialRepository.deleteById(id);
    }

    public void changeMasterPassword(int userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!CryptoUtil.verifyPassword(oldPassword, user.getMasterPasswordHash())) {
            throw new RuntimeException("Invalid old password");
        }

        List<Credential> credentials = credentialRepository.findByUserId(userId);
        byte[] oldKey = CryptoUtil.deriveKey(oldPassword);
        byte[] newKey = CryptoUtil.deriveKey(newPassword);

        for (Credential c : credentials) {
            try {
                String plain = CryptoUtil.decrypt(c.getEncryptedPassword(), oldKey);
                String newEncrypted = CryptoUtil.encrypt(plain, newKey);
                c.setEncryptedPassword(newEncrypted);
                credentialRepository.save(c);
            } catch (Exception e) {
                throw new RuntimeException("Failed to re-encrypt credential " + c.getId(), e);
            }
        }

        user.setMasterPasswordHash(CryptoUtil.hashPassword(newPassword));
        userRepository.save(user);
    }

    public List<String> generatePasswords(int count, int length, boolean useUpper, boolean useLower, boolean useDigits, boolean useSpecial, boolean excludeSimilar) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        if (excludeSimilar) {
            upper = upper.replaceAll("[OI]", "");
            lower = lower.replaceAll("[l]", "");
            digits = digits.replaceAll("[01]", "");
        }

        StringBuilder chars = new StringBuilder();
        if (useUpper) chars.append(upper);
        if (useLower) chars.append(lower);
        if (useDigits) chars.append(digits);
        if (useSpecial) chars.append(special);

        if (chars.length() == 0) return java.util.Collections.emptyList();

        SecureRandom random = new SecureRandom();
        List<String> results = new java.util.ArrayList<>();
        for (int j = 0; j < count; j++) {
            StringBuilder pass = new StringBuilder();
            for (int i = 0; i < length; i++) {
                pass.append(chars.charAt(random.nextInt(chars.length())));
            }
            results.add(pass.toString());
        }
        return results;
    }

    public List<SecurityQuestion> getSecurityQuestions(int userId) {
        return securityQuestionRepository.findByUserId(userId);
    }

    public void resetMasterPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setMasterPasswordHash(CryptoUtil.hashPassword(newPassword));
        userRepository.save(user);
    }

    public void updateSecurityQuestions(int userId, List<SecurityQuestion> questions) {
        List<SecurityQuestion> existing = securityQuestionRepository.findByUserId(userId);
        securityQuestionRepository.deleteAll(existing);
        for (SecurityQuestion q : questions) {
            q.setUserId(userId);
            q.setAnswerHash(CryptoUtil.hashPassword(q.getAnswerHash()));
            securityQuestionRepository.save(q);
        }
    }

    public void toggle2FA(int userId, boolean enable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setTwoFactorEnabled(enable);
        userRepository.save(user);
    }

    public void updateProfile(User user) {
        User existing = userRepository.findById(user.getId()).orElseThrow();
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPhoneNumber(user.getPhoneNumber());
        userRepository.save(existing);
    }

    public void toggleFavorite(int credentialId) {
        Credential cred = credentialRepository.findById(credentialId).orElseThrow();
        cred.setFavorite(!cred.isFavorite());
        credentialRepository.save(cred);
    }

    public List<Credential> getCredentials(int userId, String sort, String category) {
        if (category != null && !category.isEmpty()) {
            return credentialRepository.findByUserIdAndCategory(userId, category);
        }

        if ("name".equals(sort)) {
            return credentialRepository.findByUserIdOrderByAccountNameAsc(userId);
        } else if ("newest".equals(sort)) {
            return credentialRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else if ("modified".equals(sort)) {
            return credentialRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        } else if ("favorites".equals(sort)) {
            return credentialRepository.findByUserIdAndIsFavoriteTrue(userId);
        }

        return credentialRepository.findByUserId(userId);
    }

    public long getTotalPasswords(int userId) {
        return credentialRepository.countByUserId(userId);
    }

    public long getFavoriteCount(int userId) {
        return credentialRepository.findByUserIdAndIsFavoriteTrue(userId).size();
    }

    public String analyzeStrength(String password) {
        if (password == null) return "Unknown";
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()].*")) score++;

        if (score <= 2) return "Weak";
        if (score == 3) return "Medium";
        if (score == 4) return "Strong";
        return "Very Strong";
    }

    public java.util.Map<String, Object> auditPasswords(int userId) {
        List<Credential> credentials = credentialRepository.findByUserId(userId);
        int weakCount = 0;
        int reusedCount = 0;
        java.util.Map<String, Integer> passCounts = new java.util.HashMap<>();

        for (Credential c : credentials) {
            String strength = analyzeStrength(c.getEncryptedPassword());
            if ("Weak".equals(strength)) weakCount++;

            passCounts.put(c.getEncryptedPassword(), passCounts.getOrDefault(c.getEncryptedPassword(), 0) + 1);
        }

        for (int count : passCounts.values()) {
            if (count > 1) reusedCount += count;
        }

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("total", credentials.size());
        result.put("weak", weakCount);
        result.put("reused", reusedCount);
        return result;
    }

    public String exportVault(int userId) {
        List<Credential> credentials = credentialRepository.findByUserId(userId);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(credentials);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Export failed", e);
        }
    }

    public void importVault(int userId, String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            List<Credential> imported = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<Credential>>() {});
            for (Credential c : imported) {
                c.setId(0);
                c.setUserId(userId);
                credentialRepository.save(c);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("Import failed", e);
        }
    }
}
