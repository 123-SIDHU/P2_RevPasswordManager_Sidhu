package com.rev.app.controller;

import com.rev.app.entity.User;
import com.rev.app.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

    @Mock
    private AuthUtil authUtil;

    @Mock
    private Model model;

    private IndexController indexController;

    @BeforeEach
    void setUp() {
        indexController = new IndexController(authUtil);
    }

    @Test
    void testIndex() {
        User user = new User();
        user.setUsername("testuser");
        when(authUtil.getCurrentUser()).thenReturn(user);

        String view = indexController.index(model);

        assertEquals("index", view);
        verify(model).addAttribute("user", user);
    }
}
