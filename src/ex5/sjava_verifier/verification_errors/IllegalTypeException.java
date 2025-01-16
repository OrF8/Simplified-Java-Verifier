package ex5.sjava_verifier.verification_errors;

/**
 * Represents an illegal type exception.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class IllegalTypeException extends RuntimeException {

    private static final String ERROR_PREFIX_WITH_LINE = "Illegal type in line %d -> %s";

    /**
     * Constructs an illegal type exception with the given message.
     * @param message The message of the exception.
     */
    public IllegalTypeException(String message) {
        super(String.format(message));
    }

    /**
     * Constructs an illegal type exception with the given message and line number.
     * @param message The message of the exception.
     * @param lineNumber The line number where the error occurred.
     */
    public IllegalTypeException(String message, int lineNumber) {
        super(String.format(ERROR_PREFIX_WITH_LINE, lineNumber, message));
    }

}
