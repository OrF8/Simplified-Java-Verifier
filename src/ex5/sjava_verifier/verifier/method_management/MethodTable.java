package ex5.sjava_verifier.verifier.method_management;

import ex5.sjava_verifier.verifier.variable_management.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for finding valid method declarations and saving them for future usage.
 * <p>
 *     Maps the method name to a map of variables (its parameter list).
 * </p>
 * <p>
 *      It Will not allow two methods with the same name, according to sjava rules.
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
class MethodTable {

    // Errors
    private final static String EXISTING_METHOD_NAME = "Method named %s already exists.";
    private final static String METHOD_DOES_NOT_EXIST = "Method named %s does not exist.";
    private final static String WRONG_PARAM_TYPE = "Wrong parameter type. Parameter number %d is supposed " +
                                                   "to be %s, but got %s instead.";
    private final static String WRONG_PARAM_NUMBER = "Wrong number of parameters." +
                                                     "Method %s expected to get %d parameters, but got %d.";

    // Fields
    private final Map<String, List<Variable>> methods = new HashMap<>();

    /**
     * Constructor for a MethodTable object.
     */
    MethodTable() {}

    /**
     * Given a name of a method and a list of variables, checks if the method exists and
     * if the parameters match.
     *
     * @param name The name of the method.
     * @param vars The list of variables to check.
     * @return {@code true} If the method exists and the parameters match. (will throw exception otherwise)
     * @throws MethodException If the method does not exist, the number of parameters is wrong,
     *                         or the types are wrong.
     */
    boolean paramListMatches(String name, List<Variable> vars) throws MethodException {
        List<Variable> params = methods.get(name);
        if (params == null) { // If method does not exist
            throw new MethodException(String.format(METHOD_DOES_NOT_EXIST, name));
        }
        int desiredSize = params.size(), actualSize = vars.size();
        if (desiredSize != actualSize) {
            throw new MethodException(String.format(WRONG_PARAM_NUMBER, name, desiredSize, actualSize));
        }
        for (int i = 0; i < desiredSize; i++) {
            Variable param = params.get(i), actual = vars.get(i);
            if (!param.isSameType(actual)) {
                throw new MethodException(
                        String.format(WRONG_PARAM_TYPE, i + 1, param.getType(), actual.getType())
                );
            }
            // if (actual.isFinal()) TODO: Check finality
        }
        return true;
    }

    /**
     * Adds a method to the method table.
     * <p>
     *     If a method with the same name already exists, throws an exception.
     *     Otherwise, adds the method to the table.
     * </p>
     * @param name The name of the method.
     * @param paramList The list of variables that are the parameters of the method.
     * @throws MethodException If a method with the same name already exists.
     */
    void addMethod(String name, List<Variable> paramList) throws MethodException {
        if (methods.containsKey(name)) { // Two methods with the same name cannot exist
            throw new MethodException(String.format(EXISTING_METHOD_NAME, name));
        }
        methods.put(name, paramList); // The name is not taken
    }

    // TODO: For our usage, delete before submission
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String name: methods.keySet()) {
            stringBuilder.append(methods.get(name)).append("\n");
        }
        return stringBuilder.toString();
    }

}
