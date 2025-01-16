package ex5.sjava_verifier.verifier;

/**
 * Represents a type of variable in a .sjava file.
 * <p>
 *     This enum represents the different types of variables that can be declared in a .sjava file.
 *     The types are: int, double, String, boolean, and char.
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public enum VarType {
    /** Represents an integer variable. */
    INT,
    /** Represents a double variable. */
    DOUBLE,
    /** Represents a string variable. */
    STRING,
    /** Represents a boolean variable. */
    BOOLEAN,
    /** Represents a char variable. */
    CHAR;

    private static final String SJAVA_INT = "int";
    private static final String SJAVA_DOUBLE = "double";
    private static final String SJAVA_STRING = "String";
    private static final String SJAVA_BOOLEAN = "boolean";
    private static final String SJAVA_CHAR = "char";

    /**
     * Returns the variable type from the given string.
     * @param type The string representation of the variable type.
     * @return the variable type from the given string.
     */
    public static VarType fromString(String type) throws SyntaxException {
        return switch (type) {
            case SJAVA_INT -> INT;
            case SJAVA_DOUBLE -> DOUBLE;
            case SJAVA_STRING -> STRING;
            case SJAVA_BOOLEAN -> BOOLEAN;
            case SJAVA_CHAR -> CHAR;
            default -> throw new IllegalTypeException(type);
        };
    }

    /**
     * Returns the string representation of the variable type.
     * @return the string representation of the variable type.
     */
    @Override
    public String toString() {
        return switch (this) {
            case INT -> SJAVA_INT;
            case DOUBLE -> SJAVA_DOUBLE;
            case STRING -> SJAVA_STRING;
            case BOOLEAN -> SJAVA_BOOLEAN;
            case CHAR -> SJAVA_CHAR;
        };
    }
}
