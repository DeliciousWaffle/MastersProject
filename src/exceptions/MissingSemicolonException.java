package exceptions;

/**
 * Thrown when the user doesn't have a semicolon present in their input and they decided to execute it.
 */
public class MissingSemicolonException extends Exception {
    public MissingSemicolonException(String errorMessage) {
        super(errorMessage);
    }
}