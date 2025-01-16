package ex5.sjava_verifier.verification_errors;

/**
 * Represents an exception thrown when an error occurs in the variable declaration process,
 * or the variable is being modified illegally.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class VarException extends RuntimeException {

    private static final String ERROR_PREFIX = "Variable error in line %d -> %s";

    /**
     * Constructs a new VarException with the specified detail message.
     * @param message The detail message.
     */
    public VarException(String message) {
        super(message);
    }

    /**
     * Constructs a new VarException with the specified detail message and line number.
     * @param message The detail message.
     * @param lineNumber The line number where the error occurred.
     */
    public VarException(String message, int lineNumber) {
        super(String.format(ERROR_PREFIX, lineNumber, message));
    }

}
