package ex5.sjava_verifier.verifier.method_management;

import ex5.sjava_verifier.verification_errors.MethodException;
import ex5.sjava_verifier.verification_errors.SyntaxException;
import ex5.sjava_verifier.verifier.CodeVerifier;
import ex5.sjava_verifier.verifier.VarType;

import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: DOCS
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class MethodVerifier {

    // Error messages
    private static final String INVALID_METHOD_DEC = "Invalid method declaration.";
    private static final String INVALID_PARAMETER_LIST = "Method declaration with an invalid parameter list.";
    private static final String INVALID_METHOD_NAME = "Invalid method name.";
    private static final String MISSING_CURLY_BRACKET = "Method declaration does not end with a '{'.";

    // Constants
    private static final int NAME_GROUP = 1;
    private static final int PARAM_GROUP = 2;
    private static final String VOID_KEYWORD = "void";
    private static final String TYPE_REGEX = CodeVerifier.TYPE_REGEX;
    private static final String OPEN_CURLY_BRACKET = "{";
    private static final String COMMA = ",";

    // RegEx
    private static final String NAME_REGEX =
            CodeVerifier.NOT_KEYWORD_REGEX + "(?!__)[a-zA-Z][a-zA-Z_\\d]*";
    private static final String PARAM_REGEX = "(" + TYPE_REGEX + ")\\s+" + NAME_REGEX;
    private static final String DEC_REGEX = "\\s*(" + NAME_REGEX + ")\\s*\\((.*)\\)\\s*\\{";

    // Pattern instances
    private static final Pattern NAME_PATTERN = Pattern.compile("(" + NAME_REGEX + ")");
    private static final Pattern PARAM_PATTERN = Pattern.compile(PARAM_REGEX);
    private static final Pattern DEC_PATTERN = Pattern.compile(DEC_REGEX);

    private final MethodTable methodTable;
    private int lineCounter;

    public MethodVerifier(Map<Integer, String> cleanLines) throws MethodException, SyntaxException {
        this.methodTable = new MethodTable();
        initializeMethodTable(cleanLines);
    }

    private void initializeMethodTable(Map<Integer, String> cleanLines)
            throws MethodException, SyntaxException {
        try {
            for (int lineNum : cleanLines.keySet()) {
                lineCounter = lineNum;
                String line = cleanLines.get(lineNum);
                if (line.startsWith(VOID_KEYWORD)) {
                    handleMethodDec(line.replaceFirst(VOID_KEYWORD, "").trim());
                }
            }
        } catch (MethodException e) {
            throw new MethodException(e.getMessage(), lineCounter);
        }
    }

    private void handleMethodDec(String line) throws MethodException, SyntaxException {
        Matcher matcher = DEC_PATTERN.matcher(line);
        if (matcher.matches()) {
            String name = matcher.group(NAME_GROUP);
            verifyMethodNameInDec(name);
            String params = matcher.group(PARAM_GROUP);
            if (params.endsWith(COMMA)) {
                throw new MethodException(INVALID_PARAMETER_LIST);
            }
            List<VarType> paramsList = verifyParamListInDec(params.split(COMMA));
            methodTable.addMethod(name, paramsList);
        } else if (!line.endsWith(OPEN_CURLY_BRACKET)) {
            throw new SyntaxException(MISSING_CURLY_BRACKET);
        } else if (!NAME_PATTERN.matcher(line).lookingAt()) {
            throw new MethodException(INVALID_METHOD_NAME);
        } else {
            throw new MethodException(INVALID_METHOD_DEC);
        }
    }

    /**
     * verifies that the method name is valid upon declaration.
     *
     * @param name the name of the method
     * @throws MethodException if the method name is invalid
     */
    private void verifyMethodNameInDec(String name) throws MethodException {
        if (!NAME_PATTERN.matcher(name).lookingAt()) {
            throw new MethodException(INVALID_METHOD_NAME);
        }
    }

    // verifies that in a method declaration all given variable types and names are valid:
    // '('
    // valid param list
    // ')'
    // NOTES:
    // remember to support empty param list ()
    // CANNOT SUPPORT VALUES e.g: ' void sum(int x = 5) { '
    private List<VarType> verifyParamListInDec(String[] params) throws MethodException {
        List<VarType> validTypesList = new java.util.ArrayList<>();
        if (params.length == 1 && params[0].trim().isEmpty()) { // empty parameter list
            return List.of();
        }
        for (String p : params) {
            Matcher matcher = PARAM_PATTERN.matcher(p.trim());
            if (matcher.matches()) {
                String type = matcher.group(1);
                validTypesList.add(VarType.fromString(type));
            } else {
                throw new MethodException(String.format(INVALID_PARAMETER_LIST));
            }
        }
        return validTypesList;
    }

    @Override
    public String toString() {
        return methodTable.toString();
    }

    public static void main(String[] args) {
        String toMatch = "void name_(String x) {";
        Map<Integer, String> map = Map.of(1, toMatch);
        MethodVerifier mv = new MethodVerifier(map);
        System.out.println(mv);
    }

}
