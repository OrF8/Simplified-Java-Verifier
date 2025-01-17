package ex5.sjava_verifier.verifier.method_management;

import ex5.sjava_verifier.verifier.CodeVerifier;
import ex5.sjava_verifier.verifier.RegexUtils;
import ex5.sjava_verifier.verifier.VarType;
import ex5.sjava_verifier.verifier.variable_management.Variable;
import ex5.sjava_verifier.verifier.variable_management.VariableVerifier;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that verifies method declarations and calls.
 * <p>
 *     It uses a {@link MethodTable} to store the method declarations.
 *     It also uses callback functions to check if a variable is in scope, add a variable to the scope,
 *     and get a variable by its name.
 *     The class also keeps track of the current line number.
 *     The class throws a {@link MethodException} if a method declaration or call is invalid.
 * </p>
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
    private static final String UNKNOWN_VARIABLE_ON_CALL = "Method %s was called with " +
                                                           "an unknown variable '%s'.";
    private static final String UNINIT_VAR_IN_CALL = "Cannot call a method with " +
                                                     "an uninitialized variable '%s'.";
    private static final String MULTIPLE_BRACKETS = "Method declaration contains multiple brackets '{'.";

    // Constants
    private static final int NAME_GROUP = 1;
    private static final int PARAM_FINAL_GROUP = 1;
    private static final int PARAM_GROUP = 2;
    private static final int PARAM_TYPE_GROUP = 2;
    private static final int PARAM_NAME_GROUP = 3;
    private static final String VOID_KEYWORD = "void";
    private static final String EMPTY_STRING = "";
    private static final String TYPE_REGEX = CodeVerifier.TYPE_REGEX;
    private static final String OPEN_CURLY_BRACKET = "{";
    private static final String COMMA = ",";

    // RegEx formats
    private static final String NAME_REGEX = VariableVerifier.NAME_REGEX;
    private static final String OPEN_PAREN = "\\s*\\(";
    private static final String PARAM_REGEX = "^(final\\s+)?(" + TYPE_REGEX + ")\\s+(" + NAME_REGEX + ")";
    private static final String DEC_REGEX = "^(" + NAME_REGEX + ")" + OPEN_PAREN + "(.*)\\)\\s*\\{";
    private static final String CALL_REGEX = "^(" + NAME_REGEX + ")" + OPEN_PAREN + "(.*)\\)\\s*";

    // Pattern instances
    /** A pattern that matches function calling sequences */
    public static final Pattern CALL_PATTERN = Pattern.compile(CALL_REGEX);
    private static final Pattern NAME_PATTERN = Pattern.compile("(" + NAME_REGEX + ")");
    private static final Pattern PARAM_PATTERN = Pattern.compile(PARAM_REGEX);
    private static final Pattern DEC_PATTERN = Pattern.compile(DEC_REGEX);

    // Private fields
    private final MethodTable methodTable;
    private final Function<String, Boolean> isVarInScopeCallback;
    private final Function<String, Variable> getVariableCallback;
    private final BiFunction<String, Variable, Void> addVarToScopeCallback;
    private long lineCounter;

    /**
     * Constructs a MethodVerifier with the given map of clean lines.
     * @param cleanLines A map where the key is the line number and the value is the cleaned line of code.
     * @param isVarInScope A callback function that checks if a variable is in scope.
     * @param addVarToScope A callback function that adds a variable to the scope.
     * @param getVariableCallback A callback function that returns a variable by its name.
     * @throws MethodException if a method declaration is invalid.
     */
    public MethodVerifier(Map<Long, String> cleanLines,
                          Function<String, Boolean> isVarInScope,
                          BiFunction<String, Variable, Void> addVarToScope,
                          Function<String, Variable> getVariableCallback) throws MethodException {
        this.methodTable = new MethodTable();
        this.isVarInScopeCallback = isVarInScope;
        this.addVarToScopeCallback = addVarToScope;
        this.getVariableCallback = getVariableCallback;
        initializeMethodTable(cleanLines);
    }

    /**
     * Handles a method call line.
     * @param line The line to handle.
     * @return {@code true} if the method call is valid, {@code false} otherwise.
     * @throws MethodException if the method call is invalid.
     */
    public boolean handleMethodCall(String line) throws MethodException {
        Matcher matcher = CALL_PATTERN.matcher(line);
        if (matcher.lookingAt()) {
            String name = matcher.group(NAME_GROUP);
            String parameters = matcher.group(PARAM_GROUP);
            if (parameters.equals(EMPTY_STRING) && methodTable.isParamLessMethod(name)) {
                // If the method has no parameters and the call has no parameters
                return true;
            }
            List<Variable> params = verifyParamListInCall(parameters.split(COMMA), name);
            return methodTable.paramListMatches(name, params);
        }
        return false;
    }

    /**
     * Starts a subroutine by adding the subroutine's parameters to the scope.
     * @param line The line to handle.
     * @return The name of the subroutine.
     */
    public String startSubroutine(String line) {
        line = line.replaceFirst(VOID_KEYWORD, EMPTY_STRING).strip();
        String name = line.split(OPEN_PAREN)[0];
        List<Variable> params = methodTable.getMethodParams(name);
        for (Variable var: params) {
            addVarToScopeCallback.apply(var.getName(), var);
        }
        return name;
    }

    /**
     * Verifies the parameters in a method call.
     * <p>
     *     Assumes the method has at least one parameter.
     * </p>
     * @param params The parameters to verify.
     * @param name The name of the method.
     * @return A list of the verified parameters as {@link Variable} objects.
     * @throws MethodException if a parameter is not in scope or is uninitialized.
     */
    private List<Variable> verifyParamListInCall(String[] params, String name) throws MethodException {
        List<Variable> varList = new ArrayList<>();
        for (String param: params) {
            param = param.strip(); // remove leading/trailing whitespace
            VarType type = RegexUtils.getConstantType(param); // check if the parameter is a constant
            if (type != null) { // if it is a constant
                varList.add(new Variable(EMPTY_STRING, type, true, type));
            } else { // if it isn't a constant
                if (isVarInScopeCallback.apply((param))) { // check if the variable is in scope
                    Variable var = getVariableCallback.apply(param);
                    if (var.isNotInitialized()) { // Can't call a method with an uninitialized variable
                        throw new MethodException(String.format(UNINIT_VAR_IN_CALL, var.getName()));
                    }
                    varList.add(var);
                } else { // if the variable isn't in scope
                    throw new MethodException(String.format(UNKNOWN_VARIABLE_ON_CALL, name, param));
                }
            }
        }
        return varList;
    }

    /**
     * Initializes the method table.
     * @param cleanLines A map where the key is the line number and the value is the cleaned line of code.
     * @throws MethodException if a method declaration is invalid.
     */
    private void initializeMethodTable(Map<Long, String> cleanLines) throws MethodException {
        try {
            for (long lineNum : cleanLines.keySet()) {
                lineCounter = lineNum;
                String line = cleanLines.get(lineNum);
                if (line.startsWith(VOID_KEYWORD)) {
                    handleMethodDec(line.replaceFirst(VOID_KEYWORD, EMPTY_STRING).strip());
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
        } else if (RegexUtils.MULTIPLE_OPEN_BRACKETS_PATTERN.matcher(line).find()) {
            throw new MethodException(MULTIPLE_BRACKETS);
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
     */
    private List<Variable> verifyParamListInDec(String[] params) throws MethodException {
        List<Variable> varList = new ArrayList<>();
        if (params.length == 1 && params[0].isBlank()) { // empty parameter list
            return List.of();
        }
        for (String p : params) {
            Matcher matcher = PARAM_PATTERN.matcher(p.strip());
            if (matcher.matches()) {
                String type = matcher.group(PARAM_TYPE_GROUP);
                String name = matcher.group(PARAM_NAME_GROUP);
                VarType paramType = VarType.fromString(type);
                varList.add(new Variable(
                        name, paramType, matcher.group(PARAM_FINAL_GROUP) != null, paramType
                ));
            } else {
                throw new MethodException(String.format(INVALID_PARAMETER_LIST));
            }
        }
        return varList;
    }

}
