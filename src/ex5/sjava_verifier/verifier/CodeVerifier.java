package ex5.sjava_verifier.verifier;

import ex5.sjava_verifier.verifier.method_management.MethodException;
import ex5.sjava_verifier.verifier.variable_management.VarException;
import ex5.sjava_verifier.verifier.method_management.MethodVerifier;
import ex5.sjava_verifier.verifier.variable_management.Scopes;

import ex5.sjava_verifier.verifier.variable_management.VariableVerifier;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * TODO: DOCS
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class CodeVerifier {
    // TODO: FOR US - When will CodeVerifier throw an exception? Only when syntax-wise this line is wrong.

    // Error messages
    private static final String ILLEGAL_LINE = "Illegal line of code: ";
    private static final String NESTED_METHOD_DEC = "Method declaration inside another method is illegal.";
    private static final String MISSING_SEMICOLON_ERROR = "Missing semicolon (;) at the end of the line.";
    private static final String MULTIPLE_STATEMENTS = "Only one statement is allowed per line.";
    private static final String MISSING_RETURN_STATEMENT = "Missing return statement at " +
                                                           "the end of the method %s.";
    private static final String INVALID_RETURN_STATEMENT = "Return statement found outside of a method.";
    private static final String ILLEGAL_METHOD_CALL = "Method call from the global scope is illegal.";

    // RegEx formats
    public static String TYPE_REGEX = "int|double|String|boolean|char";
    /** A regx that matches non-keywords sequences */
    public static final String NOT_KEYWORD_REGEX =
            "(?!\\b(?:" + TYPE_REGEX + "|void|if|return|final|while|true|false)\\b)";

    // Pattern instances
    private static final Pattern RETURN_PATTERN = Pattern.compile("^return\\s*;$");

    // Constants
    private static final String START_OF_METHOD_DEC = "void";
    private static final String SEMICOLON = ";";
    private static final String CLOSING_CURLEY_BRACKET = "}";

    // Private fields
    private final Scopes scopes = new Scopes();
    private final Map<Integer, String> cleanLines;
    private final VariableVerifier varVerifier = new VariableVerifier(
            scopes::changeVariableValue, scopes::addVariableToCurrentScope, scopes::getVariable
    );
    private final MethodVerifier methodVerifier;
    private int currentLine;
    private boolean isInMethod = false;
    private String prevLine = "";
    private String methodName;

    /**
     * Constructs a CodeVerifier with the given map of clean lines.
     *
     * @param cleanLines A map where the key is the line number and the value is the cleaned line of code.
     * @throws MethodException If a method declaration is illegal.
     */
    public CodeVerifier(Map<Integer, String> cleanLines) throws MethodException {
        this.cleanLines = cleanLines;
        methodVerifier = new MethodVerifier(
                cleanLines, scopes::isVariableInScopes,
                scopes::addVariableToCurrentScope, scopes::getVariable
        );
        System.out.println("Methods: " + methodVerifier);
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
     */
    public void verifyCode() throws VarException, IllegalTypeException {
        scopes.addScope(); // For global scope
        for (int numLine: cleanLines.keySet()) {
            currentLine = numLine;
            String line = cleanLines.get(currentLine);
            if (line != null) {
                handleLine(line);
            }
            prevLine = line;
        }
        System.out.println("Scopes:\n" + scopes); // TODO: FOR US - Delete before submission
    }

    private boolean checkForMultipleStatements(String line) {
        if (line.split(SEMICOLON).length > 1) { // a; b;
            throw new SyntaxException(MULTIPLE_STATEMENTS);
        }
        return false;
    }

    private boolean checkForMethodDec(String line) {
        if (line.startsWith(START_OF_METHOD_DEC)) {
            if (isInMethod) {
                throw new SyntaxException(NESTED_METHOD_DEC, currentLine);
            }
            scopes.addScope(); // For method scope
            methodName = methodVerifier.startSubroutine(line);
            isInMethod = true;
            return true;
        }
        return false;
    }

    private boolean checkForVarDec(String line) {
        if (!varVerifier.varDec(line)) {
            if (VariableVerifier.MISSING_SEMICOLON_DEC.matcher(line).matches()) {
                throw new SyntaxException(MISSING_SEMICOLON_ERROR);
            }
            return false;
        }
        return true;
    }

    private boolean checkForVarAssignment(String line) {
        if (varVerifier.varAssignment(line)) {
            if (!line.endsWith(SEMICOLON)) { // a = 4
                throw new SyntaxException(MISSING_SEMICOLON_ERROR);
            }
            return true;
        }
        return false;
    }

    private boolean checkInMethodStatements(String line) {
        if (isInMethod) {
            if (methodVerifier.handleMethodCall(line)) { // a()
                if (!line.endsWith(SEMICOLON)) {
                    throw new SyntaxException(MISSING_SEMICOLON_ERROR);
                }
                return true;
            } else if (line.trim().equals(CLOSING_CURLEY_BRACKET)) {
                if (!RETURN_PATTERN.matcher(prevLine).matches()) {
                    throw new SyntaxException(String.format(MISSING_RETURN_STATEMENT, methodName));
                } else {
                    isInMethod = false;
                    // TODO: FOR US - Delete before submission
                    System.out.println("Scopes before closing method " + methodName + ": " + scopes);
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
        /*
            void name(String args) {
                if (args.endsWith("a")) {
                    args = "";
                }
            }
         */
        if (RETURN_PATTERN.matcher(line).matches()) {
            if (isInMethod) {
                return;
            } else {
                throw new SyntaxException(INVALID_RETURN_STATEMENT);
            }
        } else if (MethodVerifier.CALL_PATTERN.matcher(line).lookingAt()) {
            throw new SyntaxException(ILLEGAL_METHOD_CALL);
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
     */
    private void handleLine(String line) throws VarException, IllegalTypeException,
                                                SyntaxException, MethodException {
        try { // void main(int x) {
            if (checkForMultipleStatements(line)) { return; }
            else if (checkForMethodDec(line)) { return; }
            else if (checkForVarDec(line)) { return; } // Try to handle variable assignment
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
        }
    }

}
