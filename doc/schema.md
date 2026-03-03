# Database Schema (SQL)

The RevPassword Manager uses a normalized SQL schema designed for security and integrity.

```sql
-- Sequences for Auto-Incrementing IDs
CREATE SEQUENCE pm_user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE pm_vault_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE pm_sq_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE pm_vc_seq START WITH 1 INCREMENT BY 1;

-- User Management Table
CREATE TABLE pm_users (
    id NUMBER(19,0) PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    email VARCHAR2(100) NOT NULL UNIQUE,
    full_name VARCHAR2(100),
    phone VARCHAR2(20),
    master_password_hash VARCHAR2(255) NOT NULL,
    email_verified NUMBER(1,0) DEFAULT 0 NOT NULL,
    pending_email VARCHAR2(100),
    profile_photo_url CLOB,
    totp_secret VARCHAR2(100),
    totp_enabled NUMBER(1,0) DEFAULT 0 NOT NULL,
    account_locked NUMBER(1,0) DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Password Vault Table
CREATE TABLE pm_vault_entries (
    id NUMBER(19,0) PRIMARY KEY,
    user_id NUMBER(19,0) NOT NULL,
    account_name VARCHAR2(100) NOT NULL,
    website_url VARCHAR2(255),
    account_username VARCHAR2(100),
    encrypted_password VARCHAR2(500) NOT NULL, -- AES Encrypted
    category VARCHAR2(30),
    notes VARCHAR2(1000),
    is_favorite NUMBER(1,0) DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_vault_user FOREIGN KEY (user_id) REFERENCES pm_users(id) ON DELETE CASCADE
);

-- Security Questions Table
CREATE TABLE pm_security_questions (
    id NUMBER(19,0) PRIMARY KEY,
    user_id NUMBER(19,0) NOT NULL,
    question_text VARCHAR2(255) NOT NULL,
    answer_hash VARCHAR2(255) NOT NULL, -- BCrypt Hash
    CONSTRAINT fk_sq_user FOREIGN KEY (user_id) REFERENCES pm_users(id) ON DELETE CASCADE
);

-- Verification Codes (OTP) Table
CREATE TABLE pm_verification_codes (
    id NUMBER(19,0) PRIMARY KEY,
    user_id NUMBER(19,0) NOT NULL,
    code VARCHAR2(10) NOT NULL,
    purpose VARCHAR2(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used NUMBER(1,0) DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vc_user FOREIGN KEY (user_id) REFERENCES pm_users(id) ON DELETE CASCADE
);
```
