# 🏛️ RevPassword Manager: Comprehensive Technical Architecture

## 1. Project Introduction
**RevPassword Manager (RevSecurity)** is a high-security, 4-tier web application designed to help users securely store, organize, and manage their digital credentials. Built on **Spring Boot 3** and powered by **AES-256 block-cipher encryption**, the system ensures that sensitive data is protected at rest, even if the underlying database is compromised. 

The application utilizes **Thymeleaf** for a rich administrative web interface and a stateless **REST API** with **JWT** for programmatic interactions. Security is a first-class citizen, featuring Multi-Factor Authentication (OTP), security questions for recovery, and a comprehensive security audit engine to identify weak or old passwords.

---

## 2. System Diagram (Architecture Overview)
This diagram illustrates the high-level 4-tier architecture, showing how user requests flow from the presentation layer down to the persistence layer through security and logic filters.

```mermaid
graph TD
    subgraph "Presentation Layer"
        User((User)) --> UI[Thymeleaf Portal / Modern CSS / JS]
        User --> API[REST API Consumers / Postman]
    end

    subgraph "Security Layer (Interceptors)"
        UI --> AuthFilter[Spring Security Filter Chain]
        API --> JWTFilter[JWT Authentication Filter]
        AuthFilter --> Context[Security Context / UserDetails]
        JWTFilter --> Context
    end

    subgraph "Business Logic Layer (Services)"
        Context --> Ctrl[MVC & REST Controllers]
        Ctrl --> Services[Business Brain: Vault, Auth, Audit]
        Services --> Crypt[AES-256 Encryption Engine]
    end

    subgraph "Persistence Layer (Data Access)"
        Services --> Repos[Spring Data JPA Repositories]
        Repos --> DB[(Oracle/SQL Database)]
    end
```

---

## 3. Use Case Diagram
This diagram defines the primary interactions between the **System Actor (User)** and the application's core functionality.

```mermaid
graph TD
    User((User))
    
    User --> UC1((Sign Up & OTP Verify))
    User --> UC2((Secure Login & 2FA))
    User --> UC3((Configure Recovery Questions))
    
    User --> UC4((Manage Vault Entries))
    User --> UC5((View & Decrypt Passwords))
    User --> UC6((Generate Secure Passwords))
    
    User --> UC7((Run Security Audit))
    User --> UC8((Update Profile & Master PWD))
    
    style UC1 fill:#f9f,stroke:#333
    style UC2 fill:#f9f,stroke:#333
    style UC4 fill:#bbf,stroke:#333
    style UC5 fill:#bbf,stroke:#333
    style UC7 fill:#bfb,stroke:#333
```

---

## 4. Entity Relationship (ER) Diagram
The data model is optimized for security and integrity, utilizing one-to-many relationships for user-owned assets.

```mermaid
erDiagram
    USER ||--o{ VAULT_ENTRY : "owns"
    USER ||--o{ SECURITY_QUESTION : "configures"
    USER ||--o{ VERIFICATION_CODE : "validates"

    USER {
        long id PK
        string username UK
        string email UK
        string master_password_hash "BCrypt"
        boolean totp_enabled
        boolean account_locked
        datetime created_at
    }

    VAULT_ENTRY {
        long id PK
        long user_id FK
        string account_name
        string website_url
        string encrypted_password "AES-256"
        string category "Work/Personal/Social"
        boolean is_favorite
        datetime updated_at
    }

    SECURITY_QUESTION {
        long id PK
        long user_id FK
        string question_text
        string answer_hash "BCrypt"
    }

    VERIFICATION_CODE {
        long id PK
        long user_id FK
        string code "6-Digit OTP"
        string purpose "REGISTER/LOGIN/RECOVER"
        datetime expires_at
    }
```

---

## 5. Class Diagrams (Module Breakdown)

### 📂 Module 1: Authentication & Identity Management

```mermaid
classDiagram
    class SecurityConfig {
        +filterChain(HttpSecurity)
        +passwordEncoder() BCrypt
    }
    class UserDetailsServiceImpl {
        +loadUserByUsername(String)
    }
    class AuthController {
        +register(UserDTO)
        +login(LoginRequest)
    }
    class IUserService {
        <<interface>>
        +register(User)
        +toggle2FA(Long)
    }
    class User {
        +Long id
        +String username
        +String masterPassword
        +boolean totpEnabled
    }

    SecurityConfig ..> UserDetailsServiceImpl : uses
    AuthController --> IUserService : orchestrates
    IUserService --> User : manages
```

### 📂 Module 2: Password Vault Engine

```mermaid
classDiagram
    class VaultRestController {
        +getAllEntries()
        +saveEntry(VaultEntryDTO)
    }
    class IVaultService {
        <<interface>>
        +getDecryptedEntry(Long)
        +saveWithEncryption(VaultEntry)
    }
    class VaultEntryMapper {
        +toEntity(DTO)
        +toDTO(Entity)
    }
    class IEncryptionService {
        <<interface>>
        +encrypt(String)
        +decrypt(String)
    }
    class VaultEntry {
        +Long id
        +String encryptedPassword
        +String category
    }

    VaultRestController --> IVaultService : calls
    IVaultService --> VaultEntryMapper : uses
    VaultEntryMapper --> IEncryptionService : triggers crypt
    IVaultService --> VaultEntry : persists
```

### 📂 Module 3: Security Verification & Audit

```mermaid
classDiagram
    class ISecurityAuditService {
        <<interface>>
        +generateReport(userId)
    }
    class AuditReport {
        +int securityScore
        +List weakPasswords
    }
    class IVerificationService {
        <<interface>>
        +sendOTP(User)
        +validateCode(String)
    }
    class ISecurityQuestionService {
        <<interface>>
        +verifyAnswers(List)
    }

    ISecurityAuditService --> AuditReport : produces
    ISecurityAuditService ..> VaultEntry : analyzes
    IVerificationService ..> VerificationCode : generates
    ISecurityQuestionService ..> SecurityQuestion : validates
```

---

## 🛡️ Summary of Technical Implementation
1.  **Encryption Strategy**: Uses **AES/CBC/PKCS5Padding** for reversible encryption of vault secrets; **BCrypt** for non-reversible hashing of credentials.
2.  **Stateless Security**: REST endpoints are guarded by a manual **JWT filter** ensuring no server-side session overhead for API consumers.
3.  **Data Persistence**: Uses **Spring Data JPA** with custom repository methods like `findByUserIdOrderByAccountNameAsc` to handle complex sorting and retrieval efficiently.
