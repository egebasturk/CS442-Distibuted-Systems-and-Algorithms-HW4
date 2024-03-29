/**
 * @author Alp Ege Basturk
 * Driver program which adds 4 clients to test the server
 * Warning: This is buggy, and also leaves zombies
 * */

import java.io.*;
import java.util.ArrayList;

public class Main {

    public static Process initClient()
    {
        final String javaHome = "/usr";
        final String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = Client.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className);
        //builder.redirectErrorStream();
        //builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        try {
            Process process = builder.start();
            return process;
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {
	// write your code here
        ArrayList<Process> processes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Process process = initClient();
            processes.add(process);
        }
    }
}
