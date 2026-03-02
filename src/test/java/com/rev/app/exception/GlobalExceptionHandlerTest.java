package com.rev.app.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ModelAndView mav = exceptionHandler.handleNotFound(ex);
        assertEquals("error", mav.getViewName());
        assertEquals("404", mav.getModel().get("errorCode"));
    }

    @Test
    void testHandleValidation() {
        ValidationException ex = new ValidationException("Invalid");
        ModelAndView mav = exceptionHandler.handleValidation(ex);
        assertEquals("error", mav.getViewName());
        assertEquals("400", mav.getModel().get("errorCode"));
    }
}
