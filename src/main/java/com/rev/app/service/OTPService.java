package com.rev.app.service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rev.app.entity.VerificationCode;
import com.rev.app.repository.VerificationCodeRepository;

@Service
@Transactional
public class OTPService {
    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_MS = 5 * 60 * 1000L; // 5 minutes
    private SecureRandom random = new SecureRandom();

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.rev.app.repository.UserRepository userRepository;

    public String generateOTP(int userId, String purpose) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + OTP_VALIDITY_MS);
        VerificationCode code = new VerificationCode(userId, otp.toString(), purpose, expiryTime);

        verificationCodeRepository.save(code);

        // Send Email
        userRepository.findById(userId).ifPresent(user -> {
            String subject = "Verification Code - RevPasswordManager";
            String body = "Hello " + user.getName() + ",\n\n" +
                    "Your verification code is: " + otp.toString() + "\n" +
                    "This code will expire in 5 minutes.\n\n" +
                    "Best regards,\nRevPasswordManager Team";
            emailService.sendEmail(user.getEmail(), subject, body);
        });

        return otp.toString();
    }

    public boolean validateOTP(int userId, String code, String purpose) {
        Optional<VerificationCode> codeOpt = verificationCodeRepository.findByCodeAndUserId(code, userId);

        if (codeOpt.isPresent()) {
            VerificationCode vCode = codeOpt.get();
            if (vCode.isUsed()) {
                return false;
            }
            if (vCode.getExpiryTime().before(new Timestamp(System.currentTimeMillis()))) {
                return false;
            }
            if (!vCode.getPurpose().equals(purpose)) {
                return false;
            }

            vCode.setUsed(true);
            verificationCodeRepository.save(vCode);
            return true;
        }
        return false;
    }
}
