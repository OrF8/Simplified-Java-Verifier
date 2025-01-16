package ex5.main;

import ex5.sjava_verifier.verifier.IllegalTypeException;
import ex5.sjava_verifier.verifier.method_management.MethodException;
import ex5.sjava_verifier.verifier.SyntaxException;
import ex5.sjava_verifier.verifier.variable_management.VarException;
import ex5.sjava_verifier.preprocessor.FileCleaner;
import ex5.sjava_verifier.verifier.CodeVerifier;

import java.io.IOException;
import java.util.Map;

/**
 *
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Sjavac {
    
    // Exit values
    private static final int EXIT_SUCCESS = 0; // Valid SJava file
    private static final int EXIT_FAILURE = 1; // Invalid SJava file
    private static final int EXIT_ERROR = 2; // Error rose while interrogating file validity
    
    // Error messages
    private static final String INVALID_ARG_COUNT = "Invalid number of arguments." +
                                                    " Expected 1 argument but got: %d.";
    private static final String SJAVA_FILE_ENDING = ".sjava";
    private static final String INVALID_FILE_FORMAT = "Invalid file format.";

    public static void main(String[] args) {
        try {
            if (args.length != 1) { // Throw IOException in the case of invalid argument count.
                throw new IOException(String.format(INVALID_ARG_COUNT, args.length));
            }
    
            String inputFilePath = args[0]; // path to file (if legal)
            if (!inputFilePath.endsWith(SJAVA_FILE_ENDING)) { // Make sure that file format is valid
                throw new IOException(INVALID_FILE_FORMAT);
            }
        
            // Clean the input file from valid comments, empty lines and leading/trailing whitespaces
            Map<Integer, String> fileContent = FileCleaner.cleanFile(inputFilePath);

            CodeVerifier verifier = new CodeVerifier(fileContent);
            verifier.verifyCode();

            System.exit(EXIT_SUCCESS); // No exception was raised, the file is valid. Exit with 0.
        } catch (IOException e) { // Error in reading the file or input error.
            System.err.println(e.getMessage());
            System.exit(EXIT_ERROR); // Exit with 2.
        } catch (VarException | IllegalTypeException | SyntaxException | MethodException e) {
            // TODO: Add more exceptions
            System.err.println(e.getMessage());
            System.exit(EXIT_FAILURE); // Errors were found in the verification process. Exit with 1.
        }
    }

}
