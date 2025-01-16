package ex5.sjava_verifier.verifier.method_management;

/**
 * Represents an exception thrown when an error occurs in the method declaration or calling process.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class MethodException extends RuntimeException {

    private static final String ERROR_PREFIX = "Method error in line %d -> %s";

    /**
     * Constructs a new MethodException with the specified detail message.
     * @param message The detail message.
     */
    MethodException(String message) {
        super(message);
    }

    /**
     * Constructs a new MethodException with the specified detail message and line number.
     * @param message The detail message.
     * @param lineNumber The line number where the error occurred.
     */
    public MethodException(String message, int lineNumber) {
        super(String.format(ERROR_PREFIX, lineNumber, message));
    }
}
