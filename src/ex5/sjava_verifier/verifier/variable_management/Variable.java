package ex5.sjava_verifier.verifier.variable_management;

import ex5.sjava_verifier.verifier.VarType;

/**
 * Represents a variable in a .sjava file.
 * <p>
 *     A variable has a name, a type, a flag indicating whether it is final or not,
 *     and a flag indicating whether it has been initialized or not.
 * </p>
 * <p>
 *     A variable can be initialized with a value compatible with the type of the variable,
 *     as defined in {@link VarType#areTypesCompatible(VarType, VarType)}
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Variable {

    // Error messages
    private static final String WRONG_TYPE_ASSIGNMENT = "%s is an illegal type for variable %s of type %s.";
    private static final String FINAL_VAR_ASSIGNMENT = "Trying to modify a final variable %s.";

    // Private fields
    private final String name;
    private final VarType type;
    private final boolean isFinal;
    private boolean isInitialized;

    /**
     * Constructor for a variable object.
     * Constructs a non-initialized variable object.
     * @param name The name of the variable.
     * @param type The type of the variable.
     * @param isFinal Whether the variable is final or not.
     */
    public Variable(String name, VarType type, boolean isFinal) {
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
        this.isInitialized = false;
    }

    /**
     * Constructor for a variable object.
     * Constructs an initialized variable object.
     * @param name The name of the variable.
     * @param type The type of the variable.
     * @param isFinal Whether the variable is final or not.
     * @param valueType The type of the value to assign to the variable.
     *                  The value must be of the same type as the variable.
     * @throws VarException If the value type is not compatible with the variable type.
     */
    public Variable(String name, VarType type, boolean isFinal, VarType valueType) throws VarException {
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
        setValue(valueType); // Will throw VarException in case of an incompatible type
    }

    /**
     * Changes the value of the variable.
     * The value must be of a compatible type with the variable's type,
     * as defined in {@link VarType#areTypesCompatible(VarType, VarType)}.
     * @param valueType The type of the value to assign to the variable.
     * @throws VarException If the variable is final and cannot be modified,
     *                      or if the value type is not compatible with the variable type.
     */
    public void changeValue(VarType valueType) throws VarException {
        if (!this.isFinal) {
            if (VarType.areTypesCompatible(type, valueType)) {
                this.isInitialized = true;
            } else { // Type assignment is invalid
                throw new VarException(String.format(WRONG_TYPE_ASSIGNMENT, valueType, name, type));
            }
        } else { // Trying to modify a final variable
            throw new VarException(String.format(FINAL_VAR_ASSIGNMENT, name));
        }
    }

    /**
     * @return {@code true} if the variable has not been initialized, {@code false} otherwise.
     */
    public boolean isNotInitialized() {
        return !this.isInitialized;
    }

    /**
     * Returns the type of the variable.
     * The type is one of the types defined in {@link VarType}.
     * @return The type of the variable.
     */
    public VarType getType() {
        return this.type;
    }

    /**
     * @return The name of the variable.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether another variable is of a compatible type with this variable's type,
     * as defined in {@link VarType#areTypesCompatible(VarType, VarType)}.
     * @param other The variable to check compatibility with.
     * @return {@code true} if their types are compatible, {@code false} otherwise.
     */
    public boolean isCompatibleType(Variable other) {
        return VarType.areTypesCompatible(type, other.getType());
    }

    /**
     * Sets the value of the variable.
     * @param valueType The type of the value to assign to the variable.
     * @throws VarException If the value type is not compatible with the variable type.
     */
    private void setValue(VarType valueType) throws VarException {
        if (VarType.areTypesCompatible(type, valueType)) {
            this.isInitialized = true;
        } else { // Type assignment is invalid
            throw new VarException(String.format(WRONG_TYPE_ASSIGNMENT, valueType, name, type));
        }
    }
}
