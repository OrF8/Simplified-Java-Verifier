package ex5.sjava_verifier.verifier;

import ex5.sjava_verifier.verification_errors.IllegalTypeException;
import ex5.sjava_verifier.verification_errors.SyntaxException;
import ex5.sjava_verifier.verification_errors.VarException;
import ex5.sjava_verifier.verifier.scope_manager.Scopes;
import ex5.sjava_verifier.verifier.sjava_objects.Variable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Or Forshmit
 */
class VariableVerifier {

    // Errors
    private static final String UNINITIALIZED_VAR_USAGE = "Unable to use an uninitialized variable %s.";
    private static final String NON_EXISTENT_VALUE_TYPE_ASSIGNMENT = "The type of %s is unknown.";
    private static final String UNINITIALIZED_FINAL_VAR = "final variable '%s' must be initialized.";
    private static final String ILLEGAL_VAR_ASSIGNMENT = "Illegal assignment to variable %s.";
    private static final String ILLEGAL_VAR_NAME = "Illegal variable name in: %s";
    private static final String MISSING_SEMICOLON_ERROR = "Missing semicolon (;) at the end of the line.";

    // RegEx Formats
    private static final String COMMA_SEPARATOR = ",\\s*";
    private static final String NOT_KEYWORD_REGEX = "(?!\\b(?:int|double|String|boolean|char|void|if|" +
            "return|final|while|true|false)\\b)";
    private static final String NAME_REGEX = NOT_KEYWORD_REGEX + "(?!__)[a-zA-Z_][a-zA-Z_\\d]*";
    private static final String INT_REGEX = "[-+]?\\d+";
    private static final String DOUBLE_REGEX = "[-+]?(?:\\d+\\.\\d+|\\.\\d+|\\d+\\.)"; // Will not catch int
    private static final String BOOLEAN_REGEX = "true|false"; // Will not catch int nor double
    private static final String STRING_REGEX = "\".*\"";
    private static final String CHAR_REGEX = "'.'";
    private static final String FINAL_VAR_DEC_PREFIX_REGEX = "^(final\\s+)?";
    private static final String VAR_TYPE_REGEX = "(int|double|String|boolean|char)";
    private static final String VAR_DEC_REGEX = "(" + NAME_REGEX + ")((\\s*=)(\\s*[^,;]+)?)?";
    private static final String ASSIGNMENT_PREFIX = "^(" + NAME_REGEX + ")";

    // Pattern instances
    private static final Pattern FINAL_AND_VAR_DEC_PATTERN = Pattern.compile(
            FINAL_VAR_DEC_PREFIX_REGEX + VAR_TYPE_REGEX + "\\s+(.*)"
    );
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final Pattern INT_PATTERN = Pattern.compile(INT_REGEX);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(DOUBLE_REGEX);
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile(BOOLEAN_REGEX);
    private static final Pattern STRING_PATTERN = Pattern.compile(STRING_REGEX);
    private static final Pattern CHAR_PATTERN = Pattern.compile(CHAR_REGEX);
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
    private static final String SEMICOLON = ";";

    // Private Fields
    private final Scopes scopes;

    /**
     * Constructs a VariableVerifier with the given scopes.
     * @param scopes The scopes to use.
     */
    VariableVerifier(Scopes scopes) {
        this.scopes = scopes;
    }

    /**
     * Tries to verify a variable declaration and assignment line.
     * <p>
     *     This method tries to verify a variable declaration and assignment line.
     *     If the line is a variable declaration and assignment line, it verifies it and returns true.
     *     Otherwise, it returns false.
     * </p>
     * @param line The line to verify.
     * @return {@code true} if the line is a valid variable declaration and assignment line,
     *         {@code false} otherwise.
     * @throws VarException \\TODO: Give description when
     * @throws IllegalTypeException \\TODO: Give description when
     * @throws SyntaxException If the line does not end with a semicolon.
     */
    boolean varDecAndAssignment(String line)
            throws VarException, IllegalTypeException, SyntaxException {
        Matcher matcher = FINAL_AND_VAR_DEC_PATTERN.matcher(line);
        if (matcher.lookingAt()) {
            parseDeclaration(matcher);
            if (!line.endsWith(SEMICOLON)) {
                throw new SyntaxException(MISSING_SEMICOLON_ERROR);
            }
            return true;
        }
        return false; // did not match, this line is not a variable declaration / assignment
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
     */
    boolean varAssignment(String line) { // void int method() {
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
            VarType newType = handleAssignment(matcher.group(ASSIGNMENT_VALUE_GROUP));
            scopes.changeVariableValue(name, newType);
            isFirstVariable = false;
        }
        if (!line.endsWith(SEMICOLON)) {
            throw new SyntaxException(MISSING_SEMICOLON_ERROR);
        }
        return true;
    }

    /**
     * Parse a line in which the user is trying to declare a new variable.
     * @param matcher The matcher for the line.
     * @throws VarException TODO: add cause.
     * @throws IllegalTypeException TODO: add cause.
     */
    private void parseDeclaration(Matcher matcher) throws VarException, IllegalTypeException {
        boolean isFinal = matcher.group(FINAL_KEYWORD_GROUP) != null; // Extract if final or not
        VarType type = VarType.fromString(matcher.group(TYPE_KEYWORD_GROUP)); // Extract variable type
        String[] vars = matcher.group(VARS_GROUP).split(COMMA_SEPARATOR); // Extract variable names
        for (String var : vars) { // try to create each value
            var = var.trim();
            Matcher varMatcher = VAR_DEC_PATTERN.matcher(var);
            if (!varMatcher.lookingAt()) {
                throw new VarException(String.format(ILLEGAL_VAR_NAME, var));
            }
            String name = varMatcher.group(VAR_NAME_GROUP);
            Variable variable = handleVarDeclaration(type, isFinal, name, varMatcher);
            scopes.addVariableToCurrentScope(name, variable);
        }
    }

    /**
     * Handles a variable declaration line.
     * @param matcher The matcher for the line.
     * @param isFinal Whether the variable is final or not.
     * @return The variable declared.
     * @throws VarException TODO: Add description
     * @throws IllegalTypeException TODO: Add description
     */
    private Variable handleVarDeclaration(VarType type, boolean isFinal, String name, Matcher matcher)
            throws VarException, IllegalTypeException {
        boolean isInit = matcher.group(INITIALIZATION_SYMBOL_GROUP) != null;
        VarType valueType = null;
        if (isInit) { // If there is an assignment int a=;
            String value = matcher.group(DECLARATION_VALUE_GROUP) != null ?
                           matcher.group(DECLARATION_VALUE_GROUP).trim() : null;
            if (value == null) {
                throw new VarException(String.format(ILLEGAL_VAR_ASSIGNMENT, name));
            }
            valueType = handleAssignment(value);
        } else if (isFinal) { // If a final variable was declared without assignment
            throw new VarException(String.format(UNINITIALIZED_FINAL_VAR, name));
        }
        return valueType != null ? new Variable(name, type, isFinal, valueType) :
                new Variable(name, type, isFinal);
    }

    /**
     * Handles an assignment line.
     * @param toAssign The value to assign.
     * @return The type of the value assigned.
     * @throws VarException If a variable is assigned a non-existent variable or an uninitialized variable.
     */
    private VarType handleAssignment(String toAssign) throws VarException {
        if (NAME_PATTERN.matcher(toAssign).lookingAt()) { // for lines such as "int x = y;"
            Variable assignmentVar;
            assignmentVar = scopes.getVariable(toAssign);
            if (!assignmentVar.isInitialized()) { // if variable y is not initialized
                throw new VarException(String.format(UNINITIALIZED_VAR_USAGE, toAssign));
            }
            return assignmentVar.getType();
        }
        return processValue(toAssign);  // for lines such as "int x = 5;"
    }

    /**
     * Processes the value to assign.
     * @param toAssign The value to assign.
     * @return The type of the value assigned.
     * @throws IllegalTypeException If the value is of an illegal type.
     */
    private VarType processValue(String toAssign) throws IllegalTypeException {
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
