package ex5.sjava_verifier.verifier.condition_management;

/**
 * Represents ConditionException in a .sjava file.
 * This exception is thrown when a condition is invalid.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class ConditionException extends RuntimeException {

    private static final String INVALID_CONDITION = "ConditionException in line %d -> %s";

    /**
     * Constructs a new condition exception with the given message.
     * @param message The message of the exception.
     */
    public ConditionException(String message) {
        super(message);
    }

    /**
     * Constructs a new condition exception with the given message and line number.
     * @param message The message of the exception.
     * @param line The line number where the exception occurred.
     */
    public ConditionException(String message, long line) {
        super(String.format(INVALID_CONDITION, line, message));
    }
}
