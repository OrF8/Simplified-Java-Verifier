package ex5.sjava_verifier.verifier;

/**
 * Represents a type of variable in a .sjava file.
 * <p>
 *     This enum represents the different types of variables that can be declared in a .sjava file.
 *     The types are: int, double, String, boolean, and char.
 * </p>
 * <p>
 *     The enum provides methods to convert a string to a variable type, check if two types are compatible,
 *     and get the string representation of a variable type.
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public enum VarType {
    /** Represents an integer variable type. */
    INT,
    /** Represents a double variable type. */
    DOUBLE,
    /** Represents a string variable type. */
    STRING,
    /** Represents a boolean variable type. */
    BOOLEAN,
    /** Represents a char variable type. */
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
     * @throws IllegalTypeException if the given string does not represent a valid variable type.
     */
    public static VarType fromString(String type) throws IllegalTypeException {
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
     * Returns whether the two given types are compatible.
     * @param type1 The first type.
     * @param type2 The second type.
     * @return {@code true} if the two types are compatible, {@code false} otherwise.
     */
    public static boolean areTypesCompatible(VarType type1, VarType type2) {
        return switch (type1) {
            case INT -> type2 == VarType.INT;
            case DOUBLE -> type2 == VarType.DOUBLE || type2 == VarType.INT;
            case BOOLEAN -> type2 == VarType.BOOLEAN || type2 == VarType.INT || type2 == VarType.DOUBLE;
            case STRING -> type2 == VarType.STRING;
            case CHAR -> type2 == VarType.CHAR;
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
