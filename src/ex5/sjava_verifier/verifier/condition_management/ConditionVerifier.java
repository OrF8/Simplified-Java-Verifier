package ex5.sjava_verifier.verifier.condition_management;

import ex5.sjava_verifier.verifier.RegexUtils;
import ex5.sjava_verifier.verifier.VarType;
import ex5.sjava_verifier.verifier.variable_management.Variable;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that verifies the structure of conditions in the code.
 * <p>
 *     A valid condition is structured as:
 *     <p>
 *         'if/while (conditions) {'
 *         <p>
 *             where 'conditions' is a boolean expression.
 *             <p>
 *                 A boolean expression is a series of boolean values connected by '||' or '&&'.
 *                 <p>
 *                     A boolean value can be a boolean variable, a boolean constant.
 *                     <p>
 *                         A boolean variable is a variable of type boolean, an integer or a double.
 *                     </p>
 *                     <p>
 *                          A boolean constant is a constant of type boolean, an integer or a double.
 *                     </p>
 *                 </p>
 *             </p>
 *         </p>
 *     </p>
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class ConditionVerifier {

    // Errors
    private static final String ENDS_IN_OR_AND = "Condition must end with a boolean value.";
    private static final String STARTS_IN_OR_AND = "Condition must start with a boolean value.";
    private static final String NOT_BOOLEAN_TYPE = "Conditions must be of boolean compatible type (boolean," +
                                                   " int, double). Condition %d is of type '%s' instead.";
    private static final String EMPTY_CONDITIONS = "Condition cannot be empty.";
    private static final String UNINITIALIZED_VARIABLE = "Cannot use uninitialized variable '%s' " +
                                                         "in a condition.";

    // Constants
    /** The index of the group that holds the type of the condition (if/while) */
    public static final int TYPE_GROUP = 1;
    private static final int CONDITIONS_GROUP = 2;
    private static final String OR_SIGN = "||";
    private static final String AND_SIGN = "&&";

    // RegEx formats
    private static final String OPEN_CURLY_BRACKET_REGEX = "\\{";
    private static final String CONDITION_WITHOUT_CLOSING_BRACKET_REGEX = "^(while|if)\\s*\\(\\s*(.*)\\)\\s*";
    private static final String CONDITION_STATEMENT_REGEX =
            CONDITION_WITHOUT_CLOSING_BRACKET_REGEX + OPEN_CURLY_BRACKET_REGEX;
    private static final String SPLIT_CONDITIONS_REGEX = "\\|\\||&&";

    // Pattern instances
    /** A pattern that matches a conditional statement without a curly bracket */
    public static final Pattern CONDITION_WITHOUT_CLOSING_BRACKET_PATTERN = Pattern.compile(
            CONDITION_WITHOUT_CLOSING_BRACKET_REGEX
    );
    private static final Pattern STATEMENT_PATTERN = Pattern.compile(CONDITION_STATEMENT_REGEX);

    // Private fields
    private final Function<String, Variable> getVariableCallback;

    /**
     * Constructs a new ConditionVerifier with a callback to get a variable by its name.
     * @param getVariableCallback A callback to get a variable by its name.
     */
    public ConditionVerifier(Function<String, Variable> getVariableCallback) {
        this.getVariableCallback = getVariableCallback;
    }

    /**
     * Verifies that the entire conditional line is of a valid structure:
     * <p>
     *      'if/while (conditions) {'
     * </p>
     * @param line The line to check if holds as a valid condition
     * @return {@code true} if the line is a valid conditional line, {@code false} otherwise.
     */
    public boolean verifyConditionStatement(String line) throws ConditionException {
        Matcher matcher = STATEMENT_PATTERN.matcher(line);
        if (matcher.matches()) {
            String conditions = matcher.group(CONDITIONS_GROUP).strip();
            return verifyCondition(conditions);
        }
        return false;
    }

    /**
     * Verifies that the condition(s) inside brackets '( )' are valid.
     * <p>
     *      Valid condition statements are structured as (boolean ||\&& boolean ...)
     * </p>
     * @param conditions The line to check if holds as a valid condition.
     * @return {@code true} iff the line is a valid condition.
     */
    private boolean verifyCondition(String conditions) throws ConditionException {
        if (conditions.isBlank()) { // Empty condition
            throw new ConditionException(EMPTY_CONDITIONS);
        }
        if (conditions.startsWith(OR_SIGN) || conditions.startsWith(AND_SIGN)) { // Starts with '||' or '&&'
            throw new ConditionException(STARTS_IN_OR_AND);
        }
        if (conditions.endsWith(OR_SIGN) || conditions.endsWith(AND_SIGN)) { // Ends with '||' or '&&'
            throw new ConditionException(ENDS_IN_OR_AND);
        }
        String[] conditionParts = conditions.split(SPLIT_CONDITIONS_REGEX);
        int i = 1;
        for (String part : conditionParts) {
            part = part.strip();
            VarType type = RegexUtils.getConstantType(part);
            if (type != null) { // If a constant
                if (!VarType.areTypesCompatible(VarType.BOOLEAN, type)) {
                    throw new ConditionException(String.format(NOT_BOOLEAN_TYPE, i, type));
                }
            } else { // If not a constant
                Variable var = getVariableCallback.apply(part);
                if (!VarType.areTypesCompatible(VarType.BOOLEAN, var.getType())) {
                    throw new ConditionException(String.format(NOT_BOOLEAN_TYPE, i, var.getType()));
                }
                if (var.isNotInitialized()) {
                    throw new ConditionException(String.format(UNINITIALIZED_VARIABLE, var.getName()));
                }
            }
            i++;
        }
        return true;
    }
}
