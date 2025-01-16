package ex5.sjava_verifier.verifier.method_management;

import ex5.sjava_verifier.verification_errors.MethodException;
import ex5.sjava_verifier.verifier.VarType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for finding valid method declarations and saving them for future usage.
 * Maps the method name to a map of parameter list (each variable name is assigned a type).
 * Will not allow two methods with the same name, according to sjava rules.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
class MethodTable {

    private final static String EXISTING_METHOD_NAME = "Method named %s already exists.";
    private final static String METHOD_DOES_NOT_EXIST = "Method named %s does not exist.";
    private final static String WRONG_PARAM_TYPE = "Wrong parameter type. Parameter number %d is supposed " +
                                                   "to be %s, but got %s instead.";
    private final static String WRONG_PARAM_NUMBER = "Wrong number of parameters." +
                                                     "Method %s expected to get %d parameters, but got %d.";

    // Fields
    private final Map<String, List<VarType>> methods = new HashMap<>();

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
    boolean paramListMatches(String name, List<VarType> vars) throws MethodException {
        List<VarType> params = methods.get(name);
        if (params == null) { // If method does not exist
            throw new MethodException(String.format(METHOD_DOES_NOT_EXIST, name));
        }
        int desiredSize = params.size(), actualSize = vars.size();
        if (desiredSize != actualSize) {
            throw new MethodException(String.format(WRONG_PARAM_NUMBER, name, desiredSize, actualSize));
        }
        for (int i = 0; i < desiredSize; i++) {
            VarType desiredType = params.get(i), actualType = vars.get(i);
            if (desiredType != actualType) {
                throw new MethodException(String.format(WRONG_PARAM_TYPE, i + 1, desiredType, actualType));
            }
        }
        return true;
    }

    void addMethod(String name, List<VarType> paramList) throws MethodException {
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
            stringBuilder.append(name + ": " + methods.get(name) + "\n");
        }
        return stringBuilder.toString();
    }

}
