package ex5.sjava_verifier.verifier.scope_manager;

import ex5.sjava_verifier.verification_errors.VarException;
import ex5.sjava_verifier.verifier.VarType;
import ex5.sjava_verifier.verifier.sjava_objects.Variable;

import java.util.Map;
import java.util.HashMap;

/**
 * Represents a table of variables in a specific scope.
 * <p>
 *     The table is responsible for managing the variables in a specific scope.
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
class VariableTable {

    // Error messages
    private static final String EXISTING_VAR_NAME = "A variable named %s already exists in this scope.";

    // Private fields
    private final Map<String, Variable> variables; // Maps name to a Variable

    VariableTable() {
        variables = new HashMap<>();
    }

    /**
     * Adds a variable to the table.
     * @param name The name of the variable.
     * @param variable The variable to add.
     * @throws VarException If a variable with the same name already exists in the table.
     */
    void addVariableToTable(String name, Variable variable) throws VarException {
        if (variables.containsKey(name)) { // Two variables with the same name cannot exist in the same scope
            throw new VarException(String.format(EXISTING_VAR_NAME, name));
        }
        variables.put(name, variable); // The name is not taken
    }

    /**
     * Returns the variable with the given name.
     * @param name The name of the variable to return.
     * @throws VarException If the type of the variable is not the same as the given type.
     */
    void changeVariableValue(String name, VarType type) throws VarException {
        variables.get(name).changeValue(type); // informs the variable to try to change to a new value
    }

    /**
     * Checks if a variable with the given name exists in the table.
     * @param name The name of the variable to check.
     * @return {@code true} if the variable exists in the table, {@code false} otherwise.
     */
    boolean isVariableInTable(String name) {
        return variables.containsKey(name);
    }

    /**
     * Returns the variable with the given name.
     * @param name The name of the variable to return.
     * @return The variable with the given name.
     */
    Variable getVariable(String name) {
        return variables.get(name);
    }

    // TODO: This is for us, delete before submission
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String varName : variables.keySet()){
            sb.append("\t\t");
            sb.append(variables.get(varName).toString());
        }
        return sb.toString();
    }

}
