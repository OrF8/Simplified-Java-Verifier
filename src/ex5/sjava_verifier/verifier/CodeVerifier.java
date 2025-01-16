package ex5.sjava_verifier.verifier;

import ex5.sjava_verifier.verifier.method_management.MethodException;
import ex5.sjava_verifier.verifier.variable_management.VarException;
import ex5.sjava_verifier.verifier.method_management.MethodVerifier;
import ex5.sjava_verifier.verifier.variable_management.Scopes;

import ex5.sjava_verifier.verifier.variable_management.VariableVerifier;

import java.util.Map;

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
    private static final String NESTED_METHOD_DEC = "Method declaration inside another method.";
    private static final String MISSING_SEMICOLON_ERROR = "Missing semicolon (;) at the end of the line.";
    private static final String MULTIPLE_STATEMENTS = "Only one statement is allowed per line.";

    // RegEx formats
    public static String TYPE_REGEX = "int|double|String|boolean|char";
    /** A regx that matches non-keywords sequences */
    public static final String NOT_KEYWORD_REGEX =
            "(?!\\b(?:" + TYPE_REGEX + "|void|if|return|final|while|true|false)\\b)";

    // Pattern instances

    // Constants
    private static final String START_OF_METHOD_DEC = "void";
    private static final String SEMICOLON = ";";

    // Private fields
    private final Scopes scopes = new Scopes();
    private final Map<Integer, String> cleanLines;
    private final VariableVerifier varVerifier = new VariableVerifier(
            scopes::changeVariableValue, scopes::addVariableToCurrentScope, scopes::getVariable
    );
    private final MethodVerifier methodVerifier;
    private int currentLine;
    private boolean isInMethod = false;

    /**
     * Constructs a CodeVerifier with the given map of clean lines.
     *
     * @param cleanLines A map where the key is the line number and the value is the cleaned line of code.
     */
    public CodeVerifier(Map<Integer, String> cleanLines) throws MethodException, SyntaxException {
        this.cleanLines = cleanLines;
        methodVerifier = new MethodVerifier(cleanLines);
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
                if (line.startsWith(START_OF_METHOD_DEC)) {
                    if (isInMethod) {
                        throw new SyntaxException(NESTED_METHOD_DEC, currentLine);
                    }
                    scopes.addScope(); // For method scope
                    isInMethod = true;
                    continue; // Method verifier ensures that all method declarations are valid, so skip them.
                }
                handleLine(line);
            }
        }
        System.out.println("Scopes:\n" + scopes); // TODO: FOR US - Delete before submission
    }

    /**
     * Handles a line of code.
     * @param line The line of code to handle.
     * @throws VarException If a variable is declared with an illegal name or is not initialized.
     * @throws IllegalTypeException If a variable is declared with an illegal type.
     */
    private void handleLine(String line) throws VarException, IllegalTypeException, SyntaxException {
        try {
            if (line.split(SEMICOLON).length > 1) {
                throw new SyntaxException(MULTIPLE_STATEMENTS);
            }
            if (varVerifier.varDec(line)) { // Try to handle variable declaration
                return;
            } else if (varVerifier.varAssignment(line)) { // Try to handle variable assignment
                if (!line.endsWith(SEMICOLON)) {
                    throw new SyntaxException(MISSING_SEMICOLON_ERROR);
                }
            } else {
                handleIllegalLine(line);
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

    /**
     * Handles an illegal line of code.
     * <p>
     *     This method is called when a line of code is illegal.
     * </p>
     * @param line The illegal line of code.
     * @throws SyntaxException That represents the problem with the line.
     */
    private void handleIllegalLine(String line) throws SyntaxException {
        // TODO: handle illegal line (method type must be void, semicolon at the end, etc.)
        throw new SyntaxException(ILLEGAL_LINE + line);
    }

}
