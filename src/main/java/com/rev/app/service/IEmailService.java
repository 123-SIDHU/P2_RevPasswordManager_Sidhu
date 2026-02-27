package com.rev.app.service;

public interface IEmailService {
    void sendOtp(String toEmail, String otp, String purpose);
}
