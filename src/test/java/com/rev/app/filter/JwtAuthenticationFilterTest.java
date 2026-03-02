package com.rev.app.filter;

import com.rev.app.config.UserDetailsServiceImpl;
import com.rev.app.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Test
    void testDoFilterInternal_NoApiPrefix() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/dashboard");
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ValidJwt() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/vault");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_jwt");
        when(jwtUtil.extractUsername("valid_jwt")).thenReturn("user1");
        
        UserDetails userDetails = new User("user1", "pass", new ArrayList<>());
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(jwtUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
