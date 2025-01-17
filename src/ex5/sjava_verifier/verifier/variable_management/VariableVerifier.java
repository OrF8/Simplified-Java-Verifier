package ex5.sjava_verifier.verifier.variable_management;

import ex5.sjava_verifier.verifier.CodeVerifier;
import ex5.sjava_verifier.verifier.VarType;
import ex5.sjava_verifier.verifier.RegexUtils;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that verifies variable declarations and assignments.
 * <p>
 *     This class is responsible for verifying variable declarations and assignments.
 *     It uses regular expressions to verify the syntax of the lines.
 *     It also uses a set of callbacks to change the value of a variable,
 *     add a new variable to the list of variables, and get a variable by its name.
 * </p>
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class VariableVerifier {

    // Errors
    private static final String UNINITIALIZED_VAR_USAGE = "Unable to use an uninitialized variable %s.";
    private static final String UNINITIALIZED_FINAL_VAR = "final variable '%s' must be initialized.";
    private static final String ILLEGAL_VAR_ASSIGNMENT = "Illegal assignment to variable %s.";
    private static final String ILLEGAL_VAR_NAME = "'%s' is an illegal variable name.";
    private static final String ILLEGAL_COMMA = "Expected a variable name after ','.";

    // RegEx Formats
    private static final String NOT_KEYWORD_REGEX =
            "(?!\\b(?:" + CodeVerifier.TYPE_REGEX + "|void|if|return|final|while|true|false)\\b)";
    /** RegEx for a valid variable name in s-java. */
    public static final String NAME_REGEX =
            NOT_KEYWORD_REGEX + "(?!^_$)(?!__)[a-zA-Z_][a-zA-Z_\\d]*";
    private static final String COMMA_SEPARATOR = ",\\s*";
    private static final String FINAL_VAR_DEC_PREFIX_REGEX = "^(final\\s+)?";
    private static final String VAR_TYPE_REGEX = "(int|double|String|boolean|char)";
    private static final String VAR_DEC_REGEX = "(" + NAME_REGEX + ")(\\s*(=)(\\s*[^,;]+)?)?";
    private static final String ASSIGNMENT_PREFIX = "^(" + NAME_REGEX + ")";
    private static final String SPLIT_TO_GET_VAR_NAME_REGEX = "\\s*=\\s*";

    // Pattern instances
    /** A pattern that matches if a declaration statement does not end with a semicolon */
    public static final Pattern MISSING_SEMICOLON_DEC = Pattern.compile(
            FINAL_VAR_DEC_PREFIX_REGEX + VAR_TYPE_REGEX + "\\s+(.*)"
    );
    private static final Pattern FINAL_AND_VAR_DEC_PATTERN = Pattern.compile(
            FINAL_VAR_DEC_PREFIX_REGEX + VAR_TYPE_REGEX + "\\s+(.*);$"
    );
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final Pattern NOT_NAME_PATTERN = Pattern.compile("(?!" + NAME_REGEX + ")");
    private static final Pattern VAR_DEC_PATTERN = Pattern.compile(VAR_DEC_REGEX);
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile(
            ASSIGNMENT_PREFIX + "\\s*=\\s*([^,;]+)"
    );

    // Constants
    private static final int VAR_NAME_GROUP = 1;
    private static final int FINAL_KEYWORD_GROUP = 1;
    private static final int TYPE_KEYWORD_GROUP = 2;
    private static final int ASSIGNMENT_VALUE_GROUP = 2;
    private static final int VARS_GROUP = 3;
    private static final int INITIALIZATION_SYMBOL_GROUP = 3;
    private static final int DECLARATION_VALUE_GROUP = 4;
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String SEMICOLON = ";";

    // Private Fields
    private final BiFunction<String, VarType, Void> changeValueCallback;
    private final BiFunction<String, Variable, Void> addVariableCallback;
    private final Function<String, Variable> getVariableCallback;

    /**
     * Creates a new instance of the {@link VariableVerifier} class.
     * @param changeValueCallback A callback to change the value of a variable.
     * @param addVariableCallback A callback to add a new variable to the list of variables.
     * @param getVariableCallback A callback to get a variable by its name.
     */
    public VariableVerifier(BiFunction<String, VarType, Void> changeValueCallback,
                            BiFunction<String, Variable, Void> addVariableCallback,
                            Function<String, Variable> getVariableCallback) {
        this.changeValueCallback = changeValueCallback;
        this.addVariableCallback = addVariableCallback;
        this.getVariableCallback = getVariableCallback;
    }

    /**
     * Tries to verify a variable declaration line.
     * <p>
     *     This method tries to verify a variable declaration line.
     *     If the line is a variable declaration line, it verifies it and returns true.
     *     Otherwise, it returns false.
     * </p>
     * @param line The line to verify.
     * @return {@code true} if the line is a valid variable declaration and line, {@code false} otherwise.
     * @throws VarException If the line is not a valid variable declaration line.
     */
    public boolean varDec(String line) throws VarException {
        Matcher matcher = FINAL_AND_VAR_DEC_PATTERN.matcher(line);
        if (matcher.lookingAt()) {
            if (isMultipleSemicolon(line)) {
                return false;
            }
            parseDeclaration(matcher);
            return true;
        }
        return false; // did not match, this line is not a variable declaration\assignment
    }

    /**
     * Tries to verify a variable assignment line.
     * <p>
     *     This method tries to verify a variable assignment line.
     *     If the line is a variable assignment line, it verifies it and returns {@code true}.
     *     Otherwise, it returns {@code false}.
     * </p>
     * @param line The line to verify.
     * @return {@code true} if the line is a valid variable assignment line, {@code false} otherwise.
     * @throws VarException If the line is not a valid variable assignment line.
     */
    public boolean varAssignment(String line) throws VarException {
        if (line.endsWith(COMMA + SEMICOLON) || line.endsWith(COMMA)) {
            throw new VarException(ILLEGAL_COMMA);
        }
        String[] assignments = line.split(COMMA_SEPARATOR);
        boolean isFirstVariable = true;
        for (String assignment : assignments) {
            Matcher matcher = ASSIGNMENT_PATTERN.matcher(assignment);
            if (!matcher.lookingAt()) {
                if (isFirstVariable) {
                    return false; // did not match, this line is not a variable assignment
                } else { // if the first variable was already assigned, this is an illegal assignment
                    throw new VarException(String.format(ILLEGAL_VAR_NAME, assignment));
                }
            }
            String name = matcher.group(VAR_NAME_GROUP);
            VarType newType = handleAssignment(matcher.group(ASSIGNMENT_VALUE_GROUP), name);
            changeValueCallback.apply(name, newType);
            isFirstVariable = false;
        }
        return true;
    }

    private static boolean isMultipleSemicolon(String line) {
        return RegexUtils.MULTIPLE_SEMICOLON_PATTERN.matcher(line).find();
    }

    /**
     * Parse a line in which the user is trying to declare a new variable.
     * @param matcher The matcher for the line.
     * @throws VarException If the line is not a valid variable declaration line.
     */
    private void parseDeclaration(Matcher matcher) throws VarException {
        boolean isFinal = matcher.group(FINAL_KEYWORD_GROUP) != null; // Extract if final or not
        VarType type = VarType.fromString(matcher.group(TYPE_KEYWORD_GROUP)); // Extract variable type
        String variables = matcher.group(VARS_GROUP); // Extract variable names
        if (variables.endsWith(COMMA)) {
            throw new VarException(ILLEGAL_COMMA);
        }
        String[] vars = variables.split(COMMA_SEPARATOR);
        for (String var : vars) { // try to create each value
            var = var.strip();
            Matcher varMatcher = VAR_DEC_PATTERN.matcher(var);
            if (!varMatcher.matches()) {
                Matcher nameMatcher = NOT_NAME_PATTERN.matcher(var);
                if (nameMatcher.lookingAt()) { // if the name of the variable is illegal
                    var = var.split(SPLIT_TO_GET_VAR_NAME_REGEX)[0].split(SPACE)[0];
                }
                throw new VarException(
                        String.format(ILLEGAL_VAR_NAME, var.split(SPLIT_TO_GET_VAR_NAME_REGEX)[0].strip())
                );
            }
            String name = varMatcher.group(VAR_NAME_GROUP);
            if (!NAME_PATTERN.matcher(name).matches()) {
                throw new VarException(String.format(ILLEGAL_VAR_NAME, name));
            }
            Variable variable = handleVarDeclaration(type, isFinal, name, varMatcher);
            addVariableCallback.apply(name, variable);
        }
    }

    /**
     * Handles a variable declaration line.
     * @param matcher The matcher for the line.
     * @param isFinal Whether the variable is final or not.
     * @return The variable declared.
     * @throws VarException If a final variable is not initialized, or if a variable is assigned to itself.
     */
    private Variable handleVarDeclaration(VarType type, boolean isFinal, String name, Matcher matcher)
            throws VarException {
        String equalPart = matcher.group(INITIALIZATION_SYMBOL_GROUP);
        boolean isInit = equalPart != null;
        VarType valueType = null;
        if (isInit) { // If there is an assignment int a=;
            String groupVal = matcher.group(DECLARATION_VALUE_GROUP);
            String value = groupVal != null ?
                           groupVal.strip() : null;
            if (value == null) {
                throw new VarException(String.format(ILLEGAL_VAR_ASSIGNMENT, name));
            }
            valueType = handleAssignment(value, name);
        } else if (isFinal) { // If a final variable was declared without assignment
            throw new VarException(String.format(UNINITIALIZED_FINAL_VAR, name));
        }
        return valueType != null ? new Variable(name, type, isFinal, valueType) :
                new Variable(name, type, isFinal);
    }

    /**
     * Handles an assignment line.
     * @param toAssign The value to assign.
     * @param name The name of the variable to assign to.
     * @return The type of the value assigned.
     * @throws VarException If a variable is assigned a non-existent variable, or an uninitialized variable,
     *                      or if a variable is assigned to itself.
     */
    private VarType handleAssignment(String toAssign, String name) throws VarException {
        if (NAME_PATTERN.matcher(toAssign).lookingAt()) { // for lines such as "int x = y;"
            Variable assignmentVar;
            assignmentVar = getVariableCallback.apply(toAssign);
            if (name.equals(assignmentVar.getName())) { // Do not allow int a = a; or a = a;
                throw new VarException(String.format(ILLEGAL_VAR_ASSIGNMENT, name));
            }
            if (assignmentVar.isNotInitialized()) { // if variable y is not initialized
                throw new VarException(String.format(UNINITIALIZED_VAR_USAGE, toAssign));
            }
            return assignmentVar.getType();
        }
        return RegexUtils.processValue(toAssign);  // for lines such as "int x = 5;"
    }

}
