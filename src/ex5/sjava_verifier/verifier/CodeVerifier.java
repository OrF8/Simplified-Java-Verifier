package ex5.sjava_verifier.verifier;

import ex5.sjava_verifier.verification_errors.IllegalTypeException;
import ex5.sjava_verifier.verification_errors.VarException;
import ex5.sjava_verifier.verifier.scope_manager.Scopes;
import ex5.sjava_verifier.verifier.sjava_objects.Variable;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class CodeVerifier {
    // TODO: FOR US - When will CodeVerifier throw an exception? Only when syntax-wise this line is wrong.
    // TODO: Final variable MUST BE INITIALIZED
    // TODO: Variable name is illegal

    // Error messages
    private static final String ILLEGAL_VAR_NAME = "%s is not a legal variable name.";

    // ?(int|double|String|boolean|char)\s+(%s)(\s*=)?([^;]*);$
    // RegEx formats
    /** A regex for a valid name */
    public static final String NAME_REGEX = "(?!__)[a-zA-Z_][a-zA-Z_\\d]*";
    private static final String EXISTING_NAME_REGEX = "^" + NAME_REGEX;
    private static final String FINAL_VAR_DEC_REGEX = "^(final\\s+)";
    private static final String VAR_DEC_REGEX = ""; // TODO: Add regex for variable declaration

    // Pattern instances
    private static final Pattern FINAL_VAR_DEC_PATTERN = Pattern.compile(FINAL_VAR_DEC_REGEX);
    private static final Pattern METHOD_CALL_PATTERN = Pattern.compile(EXISTING_NAME_REGEX + "\\s*\\(");

    // Private fields
    final Scopes scopes = new Scopes();
    private final Map<Integer, String> cleanLines;
    private final VariableVerifier varVerifier = new VariableVerifier(scopes);
    private int currentLine;

    /**
     * Constructs a CodeVerifier with the given map of clean lines.
     *
     * @param cleanLines A map where the key is the line number and the value is the cleaned line of code.
     */
    public CodeVerifier(Map<Integer, String> cleanLines) {
        this.cleanLines = cleanLines;
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
        for (int lineNum : cleanLines.keySet()) {
            currentLine = lineNum;
            String line = cleanLines.get(lineNum);
            handleLine(line);
        }
    }

    /**
     * Handles a line of code.
     * @param line The line of code to handle.
     * @throws VarException If a variable is declared with an illegal name or is not initialized.
     * @throws IllegalTypeException If a variable is declared with an illegal type.
     */
    private void handleLine(String line) throws VarException, IllegalTypeException {
        try {
            Matcher matcher = FINAL_VAR_DEC_PATTERN.matcher(line);
            if (matcher.matches()) {
                handleVarDeclaration(matcher);
            }
        } catch (VarException e) {
            throw new VarException(e.getMessage(), currentLine);
        } catch (IllegalTypeException e) {
            throw new IllegalTypeException(e.getMessage(), currentLine);
        }
    }

    /**
     * Handles a variable declaration.
     * <p>
     *     Assumes that the line is a syntax-valid variable declaration
     *     <p>
     *        (but not necessarily valid types, e.g., int a = "hi"; is valid syntax-wise but not type-wise).
     *     </p>
     * </p>
     * @param matcher A matcher for the variable declaration.
     * @throws VarException If a variable is declared with an illegal name or is not initialized.
     * @throws IllegalTypeException If a variable is declared with an illegal type.
     */
    private void handleVarDeclaration(Matcher matcher) throws VarException, IllegalTypeException {
        String name = matcher.group(3);
        Variable var = varVerifier.handleVarDeclaration(matcher);
        scopes.addVariableToCurrentScope(name, var);
    }

}
