package ex5.sjava_verifier.verifier.variable_management;

import ex5.sjava_verifier.verifier.VarType;

import java.util.LinkedList;

/**
 * Manages the scopes of the .sjava file.
 * <p>
 *     This class is responsible for managing the scopes of the .sjava file.
 * </p>
 * <p>
 *     It keeps track of the variables declared in the scopes and allows adding and removing scopes.
 *     The class also allows adding variables to the current scope,
 *     and checking if a variable exists in the scopes.
 *     The class also allows changing the value of a variable in the scopes.
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Scopes {

    // Errors
    private static final String NON_EXISTENT_VAR = "Variable %s was not declared";

    // Private fields
    private final LinkedList<VariableTable> scopes;

    /**
     * Constructor for a Scopes object.
     * Constructs a Scopes object with an empty list of scopes.
     */
    public Scopes() {
        this.scopes = new LinkedList<>();
    }

    /**
     * Adds a new scope to the list of scopes.
     * The new scope is added to the beginning of the list.
     * The new scope is an empty scope.
     */
    public void addScope() {
        scopes.addFirst(new VariableTable());
    }

    /**
     * Removes the current scope from the list of scopes.
     */
    public void removeScope() {
        scopes.removeFirst();
    }

    /**
     * @return The number of scopes in the list of scopes.
     */
    public int size() {
        return scopes.size();
    }

    /**
     * Adds a variable to the current scope.
     * @param name The name of the variable.
     * @param variable The variable to add.
     * @throws VarException If a variable with the same name already exists in the current scope.
     */
    public Void addVariableToCurrentScope(String name, Variable variable) throws VarException {
        scopes.getFirst().addVariableToTable(name, variable);
        return null; // Return value is ignored, but needed for the lambda expression.
    }

    /**
     * Returns whether a variable with the given name exists in the scopes (current or outer scopes).
     * @param name The name of the variable to check.
     * @return {@code true} if the variable exists in the scopes, {@code false} otherwise.
     */
    public boolean isVariableInScopes(String name) {
        for (VariableTable scope : scopes) {
            if (scope.isVariableInTable(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Changes the value of a variable in the scopes (current or outer scopes).
     * @param name The name of the variable to change.
     * @param type The new type of the variable.
     * @throws VarException If the new type is incompatible with the variable type,
     *                      or if the variable does not exist in the scopes.
     */
    public Void changeVariableValue(String name, VarType type) throws VarException {
        for (VariableTable scope : scopes) {
            if (scope.isVariableInTable(name)) {
                scope.changeVariableValue(name, type); // will throw VarException for incompatible type
                return null; // Return value is ignored, but needed for the lambda expression.
            }
        }
        // Variable doesn't exist in the scopes.
        throw new VarException(String.format(NON_EXISTENT_VAR, name));
    }

    /**
     * Returns the variable with the given name.
     * @param name The name of the variable to return.
     * @return The variable with the given name.
     * @throws VarException If the variable does not exist in the scopes.
     */
    public Variable getVariable(String name) throws VarException {
        for (VariableTable scope : scopes) {
            if (scope.isVariableInTable(name)) {
                return scope.getVariable(name);
            }
        }
        throw new VarException(String.format(NON_EXISTENT_VAR, name));
    }

}
