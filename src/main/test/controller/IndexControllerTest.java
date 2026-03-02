package main.test.controller;

import com.rev.app.controller.IndexController;
import com.rev.app.entity.User;
import com.rev.app.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IndexControllerTest {

    @Mock
    private AuthUtil authUtil;

    @Mock
    private Model model;

    private IndexController indexController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
