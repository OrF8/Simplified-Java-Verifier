package ex5.sjava_verifier.verifier;

import ex5.sjava_verifier.verification_errors.IllegalTypeException;
import ex5.sjava_verifier.verification_errors.MethodException;
import ex5.sjava_verifier.verification_errors.SyntaxException;
import ex5.sjava_verifier.verification_errors.VarException;
import ex5.sjava_verifier.verifier.scope_management.Scopes;

import ex5.sjava_verifier.verifier.variable_management.VariableVerifier;
import ex5.utils.Counter;

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

    // RegEx formats
    public static String TYPE_REGEX = "int|double|String|boolean|char";
    /** A regx that matches non-keywords sequences */
    public static final String NOT_KEYWORD_REGEX =
            "(?!\\b(?:" + TYPE_REGEX + "|void|if|return|final|while|true|false)\\b)";

    // Pattern instances

    // Private fields
    final Scopes scopes = new Scopes();
    private final Map<Integer, String> cleanLines;
    private final VariableVerifier varVerifier = new VariableVerifier(
            scopes::changeVariableValue, scopes::addVariableToCurrentScope, scopes::getVariable
    );
    private final Counter currentLine;

    /**
     * Constructs a CodeVerifier with the given map of clean lines.
     *
     * @param cleanLines A map where the key is the line number and the value is the cleaned line of code.
     */
    public CodeVerifier(Map<Integer, String> cleanLines) {
        this.cleanLines = cleanLines;
        this.currentLine = new Counter();
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
        while (currentLine.getCount() <= cleanLines.size()) {
            String line = cleanLines.get(currentLine.getCount());
            if (line != null) {
                handleLine(line);
            }
            currentLine.increase();
        }
        System.out.println("Scopes:\n" + scopes);
    }

    /**
     * Handles a line of code.
     * @param line The line of code to handle.
     * @throws VarException If a variable is declared with an illegal name or is not initialized.
     * @throws IllegalTypeException If a variable is declared with an illegal type.
     */
    private void handleLine(String line) throws VarException, IllegalTypeException, SyntaxException {
        try {
            if (varVerifier.varDec(line)) { // Try to handle variable declaration
                return;
            } else if (varVerifier.varAssignment(line)) { // Try to handle variable assignment
                return;
            } else {
                throw new SyntaxException(ILLEGAL_LINE + line); // TODO: handle illegal line (method type must be void..)
            }
        } catch (VarException e) {
            throw new VarException(e.getMessage(), currentLine.getCount());
        } catch (IllegalTypeException e) {
            throw new IllegalTypeException(e.getMessage(), currentLine.getCount());
        } catch (SyntaxException e) {
            throw new SyntaxException(e.getMessage(), currentLine.getCount());
        } catch (MethodException e) {
            throw new MethodException(e.getMessage(), currentLine.getCount());
        }
    }

}
