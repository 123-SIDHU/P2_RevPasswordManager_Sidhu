package com.rev.app.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    @Qualifier("apiSecurityFilterChain")
    private SecurityFilterChain apiSecurityFilterChain;

    @Autowired
    @Qualifier("webSecurityFilterChain")
    private SecurityFilterChain webSecurityFilterChain;

    @Test
    void contextLoads() {
        assertNotNull(apiSecurityFilterChain);
        assertNotNull(webSecurityFilterChain);
    }
}
