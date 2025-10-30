package library.exception;

/**
 * Thrown when an entity cannot be located.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
