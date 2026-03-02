package main.test.rest;

import com.rev.app.dto.LoginDTO;
import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.User;
import com.rev.app.rest.AuthRestController;
import com.rev.app.service.IUserService;
import com.rev.app.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthRestControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private IUserService userService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private Authentication authentication;

    private AuthRestController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AuthRestController(authenticationManager, userService, jwtUtil, userDetailsService);
    }

    @Test
    void testLogin_Success() {
        LoginDTO dto = new LoginDTO();
        dto.setUsernameOrEmail("user");
        dto.setMasterPassword("pass");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(null);
        when(jwtUtil.generateToken(any())).thenReturn("token");

        ResponseEntity<Map<String, Object>> response = controller.login(dto, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody().get("token"));
    }

    @Test
    void testRegister_Success() {
        RegisterDTO dto = new RegisterDTO();
        User user = User.builder().username("test").build();
        when(userService.register(dto)).thenReturn(user);

        ResponseEntity<Map<String, Object>> response = controller.register(dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
