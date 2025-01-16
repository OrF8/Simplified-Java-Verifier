package ex5.sjava_verifier.verifier.method_management;

import ex5.sjava_verifier.verifier.CodeVerifier;
import ex5.sjava_verifier.verifier.VarType;
import ex5.sjava_verifier.verifier.variable_management.Variable;

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
    private static final int PARAM_FINAL_GROUP = 1;
    private static final int PARAM_GROUP = 2;
    private static final int PARAM_TYPE_GROUP = 2;
    private static final int PARAM_NAME_GROUP = 3;
    private static final String VOID_KEYWORD = "void";
    private static final String TYPE_REGEX = CodeVerifier.TYPE_REGEX;
    private static final String OPEN_CURLY_BRACKET = "{";
    private static final String COMMA = ",";

    // RegEx
    private static final String NAME_REGEX =
            CodeVerifier.NOT_KEYWORD_REGEX + "(?!__)[a-zA-Z][a-zA-Z_\\d]*";
    private static final String PARAM_REGEX = "^(final\\s+)?(" + TYPE_REGEX + ")\\s+(" + NAME_REGEX + ")";
    private static final String DEC_REGEX = "\\s*(" + NAME_REGEX + ")\\s*\\((.*)\\)\\s*\\{";

    // Pattern instances
    private static final Pattern NAME_PATTERN = Pattern.compile("(" + NAME_REGEX + ")");
    private static final Pattern PARAM_PATTERN = Pattern.compile(PARAM_REGEX);
    private static final Pattern DEC_PATTERN = Pattern.compile(DEC_REGEX);

    private final MethodTable methodTable;
    private int lineCounter;

    /**
     * Constructs a MethodVerifier with the given map of clean lines.
     * @param cleanLines A map where the key is the line number and the value is the cleaned line of code.
     * @throws MethodException if the method declaration is invalid.
     */
    public MethodVerifier(Map<Integer, String> cleanLines) throws MethodException {
        this.methodTable = new MethodTable();
        initializeMethodTable(cleanLines);
    }

    /**
     * Initializes the method table.
     * @param cleanLines A map where the key is the line number and the value is the cleaned line of code.
     * @throws MethodException if the method declaration is invalid.
     */
    private void initializeMethodTable(Map<Integer, String> cleanLines) throws MethodException {
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

    /**
     * Handles a method declaration line.
     * @param line The line to handle.
     * @throws MethodException if the method declaration is invalid.
     */
    private void handleMethodDec(String line) throws MethodException {
        Matcher matcher = DEC_PATTERN.matcher(line);
        if (matcher.matches()) {
            String name = matcher.group(NAME_GROUP);
            verifyMethodNameInDec(name);
            String params = matcher.group(PARAM_GROUP);
            if (params.endsWith(COMMA)) {
                throw new MethodException(INVALID_PARAMETER_LIST);
            }
            List<Variable> paramsList = verifyParamListInDec(params.split(COMMA));
            methodTable.addMethod(name, paramsList);
        } else if (!line.endsWith(OPEN_CURLY_BRACKET)) {
            throw new MethodException(MISSING_CURLY_BRACKET);
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

    /**
     * Verifies the parameter list in a method declaration.
     * @param params The parameters to verify.
     * @return A list of the verified parameters.
     * @throws MethodException if the parameter list is invalid.
     * @throws MethodException if the type of a parameter is invalid.
     */
    private List<Variable> verifyParamListInDec(String[] params) throws MethodException {
        List<Variable> varList = new java.util.ArrayList<>();
        if (params.length == 1 && params[0].trim().isEmpty()) { // empty parameter list
            return List.of();
        }
        for (String p : params) {
            Matcher matcher = PARAM_PATTERN.matcher(p.trim());
            if (matcher.matches()) {
                String type = matcher.group(PARAM_TYPE_GROUP);
                String name = matcher.group(PARAM_NAME_GROUP);
                varList.add(
                        new Variable(name, VarType.fromString(type), matcher.group(PARAM_FINAL_GROUP) != null)
                );
            } else {
                throw new MethodException(String.format(INVALID_PARAMETER_LIST));
            }
        }
        return varList;
    }

    @Override
    public String toString() {
        return methodTable.toString();
    } // TODO: For us, delete before submission.

}
