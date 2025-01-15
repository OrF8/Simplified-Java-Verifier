package ex5.compiler.preprocessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class is responsible for cleaning the input file from comments, empty lines,
 * and leading\trailing line white spaces. This class does not check the validity of the .sjava file,
 * and will only try to clean the file.
 *
 * <p>
 *      The class will produce a map, mapping each line number in the original file to its cleaned version.
 *      Note that comment lines will not be returned in the map, however, they will contribute to the line
 *      counting in order to keep the counting similar to the original output file.
 * </p>
 * <p>
 *     Usage:
 *          FileCleaner.cleanFile(path_to_file_you_want_to_clean)
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class FileCleaner {

    private static final String ERROR_PREFIX = "An error occurred while reading the file: ";
    private static final String COMMENT_REGEX = "^//.*";
    private static final String EMPTY_LINE_REGEX = "^\\s*$";
    private static final Pattern DISREGARD_PATTERN = Pattern.compile(
            String.format("%s|%s", COMMENT_REGEX, EMPTY_LINE_REGEX)
    );

    /**
     * An empty constructor to prevent instantiation.
     */
    private FileCleaner() {}

    /**
     * Cleans the input file by removing comments, empty lines, and leading/trailing whitespaces.
     * <p>
     *      This method reads the file line by line, removes lines that match the disregard pattern
     *      (comments or empty lines), and trims leading and trailing whitespaces from the remaining lines.
     *      The cleaned lines are stored in a map, where the key is the original line number and the value
     *      is the cleaned line content.
     * </p>
     * @param filePath the path to the file to be cleaned.
     * @return A {@link Map} containing the cleaned lines, with the original line numbers as keys.
     * @throws IOException if an error occurs while reading the file.
     */
    public static Map<Integer, String> cleanFile(String filePath) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            Map<Integer, String> validLines = new LinkedHashMap<>();
            String line;
            int lineCounter = 0;
            // Adds clear lines to the map as long as the file still has lines. Comment lines still contribute
            // to the overall line counter, but will not appear in the final map.
            while ((line = reader.readLine()) != null) {
                lineCounter++;
                if (!DISREGARD_PATTERN.matcher(line).matches()) {
                    validLines.put(lineCounter, line.strip());
                }
            }
            return validLines;
        } catch (IOException e) { // Catch the error in order to add the error prefix to its message
            throw new IOException(ERROR_PREFIX + e.getMessage());
        }
    }

}
