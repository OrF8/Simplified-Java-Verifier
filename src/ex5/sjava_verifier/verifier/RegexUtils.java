package ex5.sjava_verifier.verifier;

import java.util.regex.Pattern;

/**
 * A utility class for regular expressions.
 * This class contains regular expressions for different types of values, and methods to process them.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class RegexUtils {

    // Errors
    private static final String NON_EXISTENT_VALUE_TYPE_ASSIGNMENT = "The type of %s is unknown.";

    // RegEx formats
    /** A regular expression for an integer. */
    public static final String INT_REGEX = "[-+]?\\d+";
    /** A regular expression for a double. */
    public static final String DOUBLE_REGEX = "[-+]?(?:\\d+\\.\\d+|\\.\\d+|\\d+\\.)";
    /** A regular expression for a boolean. */
    public static final String BOOLEAN_REGEX = "true|false";
    private static final String STRING_REGEX = "\".*\"";
    private static final String CHAR_REGEX = "'.'";
    private static final String MULTIPLE_SEMICOLON_REGEX = ";;+$";
    private static final String MULTIPLE_OPEN_BRACKETS_REGEX = "\\{\\{+";

    // Pattern instances
    private static final Pattern INT_PATTERN = Pattern.compile(INT_REGEX);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(DOUBLE_REGEX);
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile(BOOLEAN_REGEX);
    private static final Pattern STRING_PATTERN = Pattern.compile(STRING_REGEX);
    private static final Pattern CHAR_PATTERN = Pattern.compile(CHAR_REGEX);
    /** A regex that finds multiple semicolons at the end of a line. */
    public static final Pattern MULTIPLE_SEMICOLON_PATTERN = Pattern.compile(MULTIPLE_SEMICOLON_REGEX);
    /** A regex that finds multiple open brackets. */
    public static final Pattern MULTIPLE_OPEN_BRACKETS_PATTERN = Pattern.compile(
            MULTIPLE_OPEN_BRACKETS_REGEX
    );

    /**
     * Returns the type of the constant to assign if it is a constant.
     * <p>
     *     Will not throw an exception.
     * </p>
     * @param toAssign The value to assign.
     * @return The type of the value assigned if it is a constant, or {@code null} otherwise.
     */
    public static VarType getConstantType(String toAssign) {
        if(INT_PATTERN.matcher(toAssign).matches() || DOUBLE_PATTERN.matcher(toAssign).matches() ||
           BOOLEAN_PATTERN.matcher(toAssign).matches() || STRING_PATTERN.matcher(toAssign).matches() ||
           CHAR_PATTERN.matcher(toAssign).matches()) {
            return processValue(toAssign);
        }
        return null;
    }

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
