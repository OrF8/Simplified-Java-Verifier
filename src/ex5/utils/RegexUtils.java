package ex5.utils;

import ex5.sjava_verifier.verification_errors.IllegalTypeException;
import ex5.sjava_verifier.verifier.VarType;

import java.util.regex.Pattern;

/**
 * @author Or Forshmit
 */
public class RegexUtils {

    // Errors
    private static final String NON_EXISTENT_VALUE_TYPE_ASSIGNMENT = "The type of %s is unknown.";

    // RegEx formats
    private static final String INT_REGEX = "[-+]?\\d+";
    private static final String DOUBLE_REGEX = "[-+]?(?:\\d+\\.\\d+|\\.\\d+|\\d+\\.)"; // Will not catch int
    private static final String BOOLEAN_REGEX = "true|false"; // Will not catch int nor double
    private static final String STRING_REGEX = "\".*\"";
    private static final String CHAR_REGEX = "'.'";

    // Pattern instances
    private static final Pattern INT_PATTERN = Pattern.compile(INT_REGEX);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(DOUBLE_REGEX);
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile(BOOLEAN_REGEX);
    private static final Pattern STRING_PATTERN = Pattern.compile(STRING_REGEX);
    private static final Pattern CHAR_PATTERN = Pattern.compile(CHAR_REGEX);

    /**
     * Processes the value to assign.
     * @param toAssign The value to assign.
     * @return The type of the value assigned.
     * @throws IllegalTypeException If the value is of an illegal type.
     */
    public static VarType processValue(String toAssign) throws IllegalTypeException {
        if (INT_PATTERN.matcher(toAssign).matches()) {
            return VarType.INT;
        } else if (DOUBLE_PATTERN.matcher(toAssign).matches()) {
            return VarType.DOUBLE;
        } else if (BOOLEAN_PATTERN.matcher(toAssign).matches()) {
            return VarType.BOOLEAN;
        } else if (STRING_PATTERN.matcher(toAssign).matches()) {
            return VarType.STRING;
        } else if (CHAR_PATTERN.matcher(toAssign).matches()) {
            return VarType.CHAR;
        } else {
            throw new IllegalTypeException(String.format(NON_EXISTENT_VALUE_TYPE_ASSIGNMENT, toAssign));
        }
    }

}
