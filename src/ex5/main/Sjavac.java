package ex5.main;

import ex5.compiler.preprocessor.FileCleaner;

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
    private static final String INVALID_ARG_COUNT = "Invalid number of arguments.";
    private static final String SJAVA_FILE_ENDING = ".sjava";
    private static final String INVALID_FILE_FORMAT = "Invalid file format.";

    public static void main(String[] args) {
        try {
            if (args.length != 1) { // TODO: Check how to verify correct input (and if needed)
                throw new IOException(INVALID_ARG_COUNT);
            }
    
            String inputFilePath = args[0];
            if (!inputFilePath.endsWith(SJAVA_FILE_ENDING)) { // Make sure that file format is valid.
                throw new IOException(INVALID_FILE_FORMAT);
            }
        
            // Clean the input file from valid comments, empty lines and leading/trailing whitespaces
            Map<Integer, String> fileContent = FileCleaner.cleanFile(inputFilePath);
            for (int lineNum: fileContent.keySet()) {
                String line = fileContent.get(lineNum);
                System.out.printf("Line %d: %s %n", lineNum, line);
            }

            System.exit(EXIT_SUCCESS);
        } catch (IOException e) { // Error in reading the file or input error
            System.err.println(e.getMessage());
            System.exit(EXIT_ERROR);
        }
    }

}
