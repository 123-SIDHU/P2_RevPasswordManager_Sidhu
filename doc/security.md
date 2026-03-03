# Security Documentation (Deep Dive)

The **RevPassword Manager** (RevSecurity) uses a **Defense in Depth** strategy to protect sensitive user data.

## 1. Non-Reversible Hashing (Identity)
- **Target**: Master Passwords, Security Question Answers.
- **Implementation**: `BCryptPasswordEncoder` (Strength: 10).
- **Explanation**: BCrypt is a non-reversible hashing algorithm that includes an automatic salt. This protects against rainbow table attacks and ensure that even if the database is compromised, the original plaintext passwords cannot be recovered.

## 2. Reversible Encryption (Data at Rest)
- **Target**: Stored Vault Credentials (Passwords).
- **Implementation**: **AES-256 (Advanced Encryption Standard)**.
- **Explanation**: User-stored passwords are encrypted in the Service layer before persistence. The secret key is managed externally (via application properties/environment variables) and never stored alongside the data. This ensures that even database administrators cannot read sensitive vault entries.

## 3. Multi-Factor Authentication (MFA)
- **Email OTP**: One-Time Passwords sent via SMTP for login and registration verification.
- **TOTP**: Time-based One-Time Passwords compatible with Google Authenticator or Authy.
- **Expiry Logic**: All OTPs have a strict 10-minute expiry window managed by the `VerificationService`.

## 4. API & Session Security
- **JWT (JSON Web Tokens)**: Used for stateless authentication of REST API calls. Signed with a secure secret using HMAC-256.
- **Spring Security 6**: Manages the authentication filter chain, CORS, CSRF protection, and session management for the web frontend.
- **Account Locking**: Automated lockout mechanism after 5 failed login attempts to prevent brute-force attacks.
