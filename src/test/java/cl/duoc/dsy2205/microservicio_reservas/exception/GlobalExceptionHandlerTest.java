package cl.duoc.dsy2205.microservicio_reservas.exception;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    @Test
    void handleValidation_returnsBadRequest() throws Exception {
        Method method = getClass().getDeclaredMethod("dummy", String.class);
        MethodParameter param = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "payload");
        bindingResult.addError(new FieldError("payload", "field", "invalid"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/reservas");

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("/api/reservas", response.getBody().getPath());
    }

    @Test
    void handleNotFound_returnsNotFound() {
        HttpServletRequest request = requestWithPath("/api/reservas/1");
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(new ResourceNotFoundException("not found"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("not found", response.getBody().getMessage());
    }

    @Test
    void handleIntegrity_returnsConflict() {
        HttpServletRequest request = requestWithPath("/api/reservas/1");
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<ErrorResponse> response = handler.handleIntegrity(new IntegrityViolationException("conflict"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("conflict", response.getBody().getMessage());
    }

    @Test
    void handleGeneric_returnsServerError() {
        HttpServletRequest request = requestWithPath("/api/reservas");
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("boom"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("boom", response.getBody().getMessage());
    }

    private void dummy(String value) { }

    private HttpServletRequest requestWithPath(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        return request;
    }
}
