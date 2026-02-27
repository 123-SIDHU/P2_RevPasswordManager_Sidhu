package com.rev.app.service;

import com.rev.app.entity.User;

public interface IVerificationService {
    String generateAndSendOtp(User user, String purpose);
    String sendRegistrationOtp(String email);
    String generateCode(User user, String purpose);
    boolean validateCode(User user, String code, String purpose);
}
