package library.exception;

/**
 * Thrown when authentication or authorization fails.
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
