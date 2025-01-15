package ex5.compiler.verifier;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class CodeVerifier {

    private final LinkedList<VariableTable> scopes = new LinkedList<>(); // Use addFirst
    private final Map<Integer, String> validLines;

    public CodeVerifier(Map<Integer, String> validLines) {
        this.validLines = validLines;
    }

}
