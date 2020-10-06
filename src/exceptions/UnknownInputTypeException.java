package exceptions;

/**
 * Thrown when the type of input cannot be determined.
 */
public class UnknownInputTypeException extends Exception {
    public UnknownInputTypeException(String message) {
        super(message);
    }
}