package ex5.sjava_verifier.verifier.sjava_objects;

import ex5.sjava_verifier.verification_errors.VarException;
import ex5.sjava_verifier.verifier.VarType;

/**
 * Represents a variable in a .sjava file.
 * A variable has a name, a type, and a final flag.
 * The variable can be initialized or not.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Variable {

    // Error messages
    private static final String WRONG_TYPE_ASSIGNMENT = "Cannot assign %s value to variable %s of type %s.";
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
     * @throws VarException If the value type is not the same as the variable type.
     */
    public Variable(String name, VarType type, boolean isFinal, VarType valueType)  throws VarException {
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
        this.isInitialized = true;
        changeValue(valueType); // Will throw VarException in case of an incompatible type
    }

    /**
     * Changes the value of the variable.
     * The value must be of the same type as the variable.
     * @param valueType The type of the value to assign to the variable.
     * @throws VarException If the value type is not the same as the variable type.
     */
    public void changeValue(VarType valueType) throws VarException {
        if (!this.isFinal) {
            if (isCompatibleType(valueType)) {
                this.isInitialized = true;
            } else { // Type assignment is invalid
                throw new VarException(String.format(WRONG_TYPE_ASSIGNMENT, valueType, name, type));
            }
        } else { // Trying to modify a final variable
            throw new VarException(String.format(FINAL_VAR_ASSIGNMENT, this.name));
        }
    }

    /**
     * Returns whether the given type is compatible with the variable type.
     * @param valueType The type to check compatibility with.
     * @return {@code true} if the types are compatible, {@code false} otherwise.
     */
    private boolean isCompatibleType(VarType valueType) {
        return switch (this.type) {
            case INT -> valueType == VarType.INT;
            case DOUBLE -> valueType == VarType.DOUBLE || valueType == VarType.INT;
            case BOOLEAN ->
                    valueType == VarType.BOOLEAN || valueType == VarType.INT || valueType == VarType.DOUBLE;
            case STRING -> valueType == VarType.STRING;
            case CHAR -> valueType == VarType.CHAR;
        };
    }
    /**
     * Returns whether the variable is initialized or not.
     * @return {@code true} if the variable is initialized, {@code false} otherwise.
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Returns the type of the variable.
     * The type is one of the types defined in the VarType enum.
     * @see VarType
     * @return The type of the variable.
     */
    public VarType getType() {
        return this.type;
    }
}
