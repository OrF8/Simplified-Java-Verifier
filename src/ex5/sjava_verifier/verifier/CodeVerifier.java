package ex5.sjava_verifier.verifier;

import ex5.sjava_verifier.verifier.method_management.MethodException;
import ex5.sjava_verifier.verifier.variable_management.VarException;
import ex5.sjava_verifier.verifier.condition_management.ConditionException;

import ex5.sjava_verifier.verifier.variable_management.Scopes;

import ex5.sjava_verifier.verifier.method_management.MethodVerifier;
import ex5.sjava_verifier.verifier.condition_management.ConditionVerifier;
import ex5.sjava_verifier.verifier.variable_management.VariableVerifier;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that verifies the code in the clean lines.
 * <p>
 *     This class verifies the code in the clean lines.
 *     It iterates over the lines and verifies each line.
 *     If an error is found, an exception is thrown.
 *     The class uses the {@link VariableVerifier}, {@link MethodVerifier} and {@link ConditionVerifier}
 *     to verify the variables, methods and conditions in the code.
 *     The class uses the {@link Scopes} to manage the scopes of the variables and methods.
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class CodeVerifier {

    // Error messages
    private static final String ILLEGAL_LINE = "Illegal line of code: ";
    private static final String NESTED_METHOD_DEC = "Method declaration inside another method is illegal.";
    private static final String MISSING_SEMICOLON = "Missing semicolon ';' at the end of the line.";
    private static final String MULTIPLE_STATEMENTS = "Only one statement is allowed per line.";
    private static final String MISSING_RETURN_STATEMENT = "Missing return statement at " +
                                                           "the end of the method %s.";
    private static final String INVALID_RETURN_STATEMENT = "Return statement found outside of a method.";
    private static final String ILLEGAL_METHOD_CALL = "Method call from the global scope is illegal.";
    private static final String CONDITION_OUT_OF_METHOD = "'if' and 'while' statements outside " +
                                                          "of a method is an illegal action.";
    private static final String MISSING_OPENING_BRACKET = "%s statement missing curly bracket '{'.";
    private static final String MISSING_CLOSING_BRACKET = "Missing closing curly bracket '}'.";
    private static final String MULTIPLE_SEMICOLON = "Line has ended with multiple semicolons ';'.";
    private static final String MULTIPLE_OPEN_BRACKETS = "Line has ended with multiple opening brackets '{'.";

    // RegEx formats
    /** A regx that matches all types */
    public static String TYPE_REGEX = "int|double|String|boolean|char";

    // Pattern instances
    private static final Pattern RETURN_PATTERN = Pattern.compile("^return\\s*;$");

    // Constants
    private static final String START_OF_METHOD_DEC = "void";
    private static final String SEMICOLON = ";";
    private static final String CLOSING_CURLEY_BRACKET = "}";

    // Final private fields
    private final Scopes scopes = new Scopes();
    private final Map<Long, String> cleanLines;
    private final VariableVerifier varVerifier;
    private final MethodVerifier methodVerifier;
    private final ConditionVerifier conditionVerifier;

    // Private fields
    private long currentLine;
    private boolean isInMethod = false;
    private String prevLine = "";
    private String methodName;

    /**
     * Constructs a CodeVerifier with the given map of clean lines.
     *
     * @param cleanLines A map where the key is the line number and the value is the cleaned line of code.
     * @throws MethodException If a method declaration is illegal.
     */
    public CodeVerifier(Map<Long, String> cleanLines) throws MethodException {
        // First, create the methodVerifier, to make sure all the method declarations are valid.
        methodVerifier = new MethodVerifier(
                cleanLines, scopes::isVariableInScopes,
                scopes::addVariableToCurrentScope, scopes::getVariable
        );
        // Then, after we know that every method declaration is valid, we can create the verifier.
        this.cleanLines = cleanLines;
        varVerifier = new VariableVerifier(
                scopes::changeVariableValue, scopes::addVariableToCurrentScope, scopes::getVariable
        );
        conditionVerifier = new ConditionVerifier(scopes::getVariable);
    }

    /**
     * Verifies the code in the clean lines.
     * <p>
     *     This method verifies the code in the clean lines.
     *     It iterates over the lines and verifies each line.
     *     If an error is found, an exception is thrown.
     * </p>
     * @throws VarException If a variable is declared with an illegal name or is not initialized.
     * @throws IllegalTypeException If a variable is declared with an illegal type.
     * @throws SyntaxException If the line is illegal.
     * @throws MethodException If a method declaration is illegal.
     * @throws ConditionException If a condition is illegal.
     */
    public void verifyCode() throws VarException, IllegalTypeException,
                                    SyntaxException, MethodException, ConditionException {
        scopes.addScope(); // For global scope
        for (long numLine: cleanLines.keySet()) {
            currentLine = numLine;
            String line = cleanLines.get(currentLine);
            if (line != null) {
                handleLine(line);
            }
            prevLine = line;
        }
        if (isInMethod || scopes.size() != 1) { // If there is a method\conditional without a closing bracket
            throw new SyntaxException(MISSING_CLOSING_BRACKET);
        }
    }

    /**
     * Checks if a line contains multiple statements.
     * @param line The line to check.
     * @return {@code true} if the line contains multiple statements, {@code false} otherwise.
     */
    private boolean checkForMultipleStatements(String line) {
        if (line.split(SEMICOLON).length > 1) { // a; b;
            throw new SyntaxException(MULTIPLE_STATEMENTS);
        }
        return false;
    }

    /**
     * Checks if the line is a method declaration.
     * <p>
     *     If the line is a method declaration, it is verified.
     *     If the line is not a method declaration, it is returned as false.
     *     If the line is a method declaration and is inside a method, an exception is thrown.
     * </p>
     * @param line The line to check if it is a method declaration.
     * @return {@code true} if the line is a method declaration, {@code false} otherwise.
     * @throws MethodException If the method declaration is illegal.
     * @throws SyntaxException If the method declaration is inside another method.
     */
    private boolean checkForMethodDec(String line) throws MethodException, SyntaxException {
        if (line.startsWith(START_OF_METHOD_DEC)) {
            if (isInMethod) {
                throw new SyntaxException(NESTED_METHOD_DEC);
            }
            scopes.addScope(); // For method scope
            methodName = methodVerifier.startSubroutine(line);
            isInMethod = true;
            return true;
        }
        return false;
    }

    /**
     * Checks if the line is a variable declaration.
     * <p>
     *     If the line is a variable declaration, it is verified.
     *     If the line is not a variable declaration, it is returned as false.
     * </p>
     * @param line The line to check if it is a variable declaration.
     * @return {@code true} if the line is a variable declaration, {@code false} otherwise.
     * @throws VarException If the variable declaration is illegal.
     * @throws SyntaxException If the variable declaration does not end with a semicolon.
     */
    private boolean checkForVarDec(String line) throws VarException, SyntaxException {
        if (!varVerifier.varDec(line)) {
            if (RegexUtils.MULTIPLE_SEMICOLON_PATTERN.matcher(line).find()) {
                throw new SyntaxException(MULTIPLE_SEMICOLON);
            }
            if (VariableVerifier.MISSING_SEMICOLON_DEC.matcher(line).matches()) {
                throw new SyntaxException(MISSING_SEMICOLON);
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if the line is a variable assignment.
     * @param line The line to check if it is a variable assignment.
     * @return {@code true} if the line is a variable assignment, {@code false} otherwise.
     * @throws VarException If the variable assignment is illegal.
     * @throws SyntaxException If the variable assignment does not end with a semicolon.
     */
    private boolean checkForVarAssignment(String line) throws VarException, SyntaxException {
        if (varVerifier.varAssignment(line)) {
            if (!line.endsWith(SEMICOLON)) {
                throw new SyntaxException(MISSING_SEMICOLON);
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the line is a conditional statement.
     * <p>
     *     If the line is a conditional statement, it is verified.
     *     If the line is a closing bracket of a conditional statement, the scope is removed.
     *     If the line is not a conditional statement, it is returned as false.
     *     If the line is a conditional statement and is not inside a method, an exception is thrown.
     * </p>
     * @param line The line to check if it is a conditional statement.
     * @return {@code true} iff the line is a conditional statement, {@code false} otherwise.
     * @throws ConditionException If the conditional statement is illegal.
     * @throws SyntaxException If the conditional statement is missing an opening bracket,
     *                         or if it is not inside a method.
     */
    private boolean checkConditional(String line) throws ConditionException, SyntaxException {
        if (!conditionVerifier.verifyConditionStatement(line)) { // The current line is not a cond statement.
            if (scopes.size() > 2 && line.strip().equals(CLOSING_CURLEY_BRACKET)) {
                scopes.removeScope(); // For condition scope
                return true;
            } else {
                Matcher matcher = ConditionVerifier.CONDITION_WITHOUT_CLOSING_BRACKET_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String type = matcher.group(ConditionVerifier.TYPE_GROUP);
                    throw new SyntaxException(String.format(MISSING_OPENING_BRACKET, type));
                }
                return false;
            }
        } else { // If matched
            if (!isInMethod) {
                throw new SyntaxException(CONDITION_OUT_OF_METHOD);
            }
            scopes.addScope(); // For condition scope
            return true;
        }
    }

    /**
     * Checks if the line is a method call or a closing bracket of a method.
     * @param line The line to check.
     * @return {@code true} if the line is a method call or a closing bracket of a method,
     *         {@code false} otherwise.
     * @throws SyntaxException If the line is missing a semicolon,
     *                         or if the method is missing a return statement.
     * @throws MethodException If the method call is illegal.
     */
    private boolean checkInMethodStatements(String line) throws SyntaxException, MethodException {
        if (isInMethod) {
            if (methodVerifier.handleMethodCall(line)) {
                if (!line.endsWith(SEMICOLON)) {
                    throw new SyntaxException(MISSING_SEMICOLON);
                }
                return true;
            } else if (line.strip().equals(CLOSING_CURLEY_BRACKET)) {
                if (!RETURN_PATTERN.matcher(prevLine).matches()) {
                    throw new SyntaxException(String.format(MISSING_RETURN_STATEMENT, methodName));
                } else {
                    isInMethod = false;
                    scopes.removeScope();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handles a suspicous line of code.
     * <p>
     *     This method is called when a line of code is suspicous.
     * </p>
     * @param line The illegal line of code.
     * @throws SyntaxException That represents the problem with the line.
     */
    private void handleSuspicousLine(String line) throws SyntaxException {
        // TODO: handle illegal line (method type must be void, semicolon at the end, etc.)
        if (RETURN_PATTERN.matcher(line).matches()) {
            if (isInMethod) {
                return;
            } else {
                throw new SyntaxException(INVALID_RETURN_STATEMENT);
            }
        } else if (MethodVerifier.CALL_PATTERN.matcher(line).lookingAt()) {
            throw new SyntaxException(ILLEGAL_METHOD_CALL);
        } else if (RegexUtils.MULTIPLE_OPEN_BRACKETS_PATTERN.matcher(line).find()) {
            throw new SyntaxException(MULTIPLE_OPEN_BRACKETS);
        }
        throw new SyntaxException(ILLEGAL_LINE + line);
    }

    /**
     * Handles a line of code.
     * @param line The line of code to handle.
     * @throws VarException If a variable is declared with an illegal name or is not initialized.
     * @throws IllegalTypeException If a variable is declared with an illegal type.
     * @throws SyntaxException If the line is illegal.
     * @throws MethodException If a method declaration is illegal.
     * @throws ConditionException If a condition is illegal.
     */
    private void handleLine(String line) throws VarException, IllegalTypeException,
                                                SyntaxException, MethodException, ConditionException {
        try {
            if (checkForMultipleStatements(line)) { return; }
            else if (checkForMethodDec(line)) { return; }
            else if (checkConditional(line)) { return; }
            else if (checkForVarDec(line)) { return; }
            else if (checkForVarAssignment(line)) { return; }
            else if (checkInMethodStatements(line)) { return; }
            else {
                handleSuspicousLine(line);
            }
        } catch (VarException e) {
            throw new VarException(e.getMessage(), currentLine);
        } catch (IllegalTypeException e) {
            throw new IllegalTypeException(e.getMessage(), currentLine);
        } catch (SyntaxException e) {
            throw new SyntaxException(e.getMessage(), currentLine);
        } catch (MethodException e) {
            throw new MethodException(e.getMessage(), currentLine);
        } catch (ConditionException e) {
            throw new ConditionException(e.getMessage(), currentLine);
        }
    }

}
