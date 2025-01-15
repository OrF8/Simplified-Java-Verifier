import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

class OutputTest
{
    public static String INPUT_PATH = "/cs/course/current/oop1/ex/ex5/tests/presubmit/input/";
    public static String EXPECTED_PATH = "/cs/course/current/oop1/ex/ex5/tests/presubmit/expected/";

    private String run(String[] args)
    {
        Class<?> sjavac_class = null;
        try
        {
            sjavac_class = Class.forName("ex5.main.Sjavac");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("Class Sjavac not found, or in wrong package.");
            System.err.println("AUTO.unimplemented_api");
            return null;
        }

        Method main = null;
        try
        {
            main = sjavac_class.getDeclaredMethod("main", String[].class);
        }
        catch(NoSuchMethodException e)
        {
            System.out.println("The main method does not exist in Sjavac.");
            System.err.println("AUTO.unimplemented_api");
            return null;
        }

        OutputStream outStream, errStream;
        try
        {
            outStream = new ByteArrayOutputStream();
            errStream = new ByteArrayOutputStream();
            PrintStream outputStreamPrint = new PrintStream(outStream);
            PrintStream errorStreamPrint = new PrintStream(errStream);
            System.setOut(outputStreamPrint);
            System.setErr(errorStreamPrint);

            main.setAccessible(true);
            main.invoke(null, (Object)args);
        }
        catch(Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            return null;
        }
        finally
        {
            // restore stdout
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
        }
        return outStream.toString();
    }

    public static void main(String[] args)
    {
        String inputFile = OutputTest.INPUT_PATH + args[0];
        String expectedFile = OutputTest.EXPECTED_PATH + args[1];

        OutputTest test = new OutputTest();
        String actualOutput = test.run(new String[] {inputFile});
        String expectedOutput = null;
        try
        {    
            expectedOutput = new String(Files.readAllBytes(Paths.get(expectedFile)));
        }
        catch (IOException e)
        {}
    
        boolean passed = false;
        if(actualOutput != null && expectedOutput != null)
        {
            // compare only first line
            actualOutput = actualOutput.split("\n")[0];
            expectedOutput = expectedOutput.split("\n")[0];
            if(!actualOutput.equals(expectedOutput))
            {
                passed = false;
            }
            else
            {
                passed = true;
            }
        }

        if(!passed)
        {
            System.out.println("==== GOT ====");
            System.out.println(actualOutput);
            System.out.println("==== EXPECTED ====");
            System.out.println(expectedOutput);
            System.err.println("AUTO.presubmit_test_failed {" + args[0] + "}");
        }
    }
}