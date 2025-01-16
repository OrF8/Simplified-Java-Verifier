package ex5.sjava_verifier.verification_errors;

/**
 * Represents a syntax error in the .sjava file.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class SyntaxException extends RuntimeException {

    private static final String ERROR_PREFIX_WITH_LINE = "Syntax error in line %d -> %s";

    /**
     * Constructs a new syntax exception with the given message.
     * @param message The message of the exception.
     */
    public SyntaxException(String message) {
        super(message);
    }

    /**
     * Constructs a new syntax exception with the given message and line number.
     * @param message The message of the exception.
     * @param line The line number where the exception occurred.
     */
    public SyntaxException(String message, int line) {
        super(String.format(ERROR_PREFIX_WITH_LINE, line, message));
    }
}
