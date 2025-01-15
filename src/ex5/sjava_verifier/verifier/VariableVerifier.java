package ex5.sjava_verifier.verifier;

import ex5.sjava_verifier.verification_errors.IllegalTypeException;
import ex5.sjava_verifier.verification_errors.VarException;
import ex5.sjava_verifier.verifier.scope_manager.Scopes;
import ex5.sjava_verifier.verifier.sjava_objects.Variable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Or Forshmit
 */
class VariableVerifier {

    private static final String UNINITIALIZED_VAR_USAGE = "Unable to use an uninitialized variable %s.";
    private static final String NON_EXISTENT_TYPE_ASSIGNMENT = "%s's type is unknown.";
    private static final String UNINITIALIZED_FINAL_VAR = "Final variable %s must be initialized.";

    // RegEx Formats
    private static final String INT_REGEX = "[-+]?\\d+";
    private static final String DOUBLE_REGEX = "[-+]?(?:\\d+\\.\\d+|\\.\\d+|\\d+\\.)"; // Will not catch int
    private static final String BOOLEAN_REGEX = "true|false"; // Will not catch int nor double
    private static final String STRING_REGEX = "\".*\"";
    private static final String CHAR_REGEX = "'.'";

    // Pattern instances
    private static final Pattern NAME_PATTERN = Pattern.compile(CodeVerifier.NAME_REGEX);
    private static final Pattern INT_PATTERN = Pattern.compile(INT_REGEX);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(DOUBLE_REGEX);
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile(BOOLEAN_REGEX);
    private static final Pattern STRING_PATTERN = Pattern.compile(STRING_REGEX);
    private static final Pattern CHAR_PATTERN = Pattern.compile(CHAR_REGEX);

    // Private Fields
    private final Scopes scopes;

    /**
     * Constructs a VariableVerifier with the given scopes.
     * @param scopes The scopes to use.
     */
    public VariableVerifier(Scopes scopes) {
        this.scopes = scopes;
    }

    /**
     * Handles a variable declaration line.
     * @param matcher The matcher for the line.
     * @param isFinal Whether the variable is final or not.
     * @return The variable declared.
     * @throws VarException TODO: Add description
     * @throws IllegalTypeException TODO: Add description
     */
    private Variable handleVarDeclaration(Matcher matcher, boolean isFinal)
            throws VarException, IllegalTypeException{
        VarType type = VarType.fromString(matcher.group(2));
        String name = matcher.group(3);
        VarType valueType = null;
        if (matcher.group(4) != null) { // If there is an assignment
            valueType = handleAssignment(matcher.group(5).strip());
        }
        return valueType != null ? new Variable(name, type, isFinal, valueType) :
                new Variable(name, type, isFinal);
    }

    /**
     * Handles a variable declaration line.
     * @param matcher The matcher for the line.
     * @return The variable declared.
     * @throws VarException TODO: Add description
     * @throws IllegalTypeException TODO: Add description
     */
    Variable handleVarDeclaration(Matcher matcher) throws VarException, IllegalTypeException {
        return handleVarDeclaration(matcher, false);
    }

    /**
     * Handles a final variable declaration line.
     * @param matcher The matcher for the line.
     * @return The variable declared.
     * @throws VarException If the final variable was not initialized.
     * @throws IllegalTypeException TODO: Add description.
     */
    Variable handleFinalVarDeclaration(Matcher matcher) throws VarException, IllegalTypeException {
        boolean isInitialized = matcher.group(4) != null;
        if (!isInitialized) { // if the final variable was not initialized
            throw new VarException(UNINITIALIZED_FINAL_VAR);
        } else {
            return handleVarDeclaration(matcher, true);
        }
    }

    /**
     * Handles an assignment line.
     * @param toAssign The value to assign.
     * @return The type of the value assigned.
     * @throws VarException If a variable is assigned a non-existent variable or an uninitialized variable.
     */
    VarType handleAssignment(String toAssign) throws VarException {
        if (NAME_PATTERN.matcher(toAssign).matches()) { // for lines such as "int x = y;"
            Variable assignmentVar;
            if (!scopes.isVariableInScopes(toAssign)) { // variable y is not in scopes
                throw new VarException(String.format(Scopes.NON_EXISTENT_VAR, toAssign));
            } else { // variable y exists
                assignmentVar = scopes.getVariable(toAssign);
                if (!assignmentVar.isInitialized()) { // if variable y is not initialized
                    throw new VarException(String.format(UNINITIALIZED_VAR_USAGE, toAssign));
                }
            }
            return assignmentVar.getType();
        }
        return processValue(toAssign);
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
            throw new IllegalTypeException(String.format(NON_EXISTENT_TYPE_ASSIGNMENT, toAssign));
        }
    }

}
