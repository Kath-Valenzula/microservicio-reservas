package cl.duoc.dsy2205.microservicio_reservas.exception;

public class IntegrityViolationException extends RuntimeException {
    public IntegrityViolationException() { super(); }
    public IntegrityViolationException(String message) { super(message); }
    public IntegrityViolationException(String message, Throwable cause) { super(message, cause); }
}
