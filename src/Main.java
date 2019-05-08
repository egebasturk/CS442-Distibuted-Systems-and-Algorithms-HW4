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
        for (int i = 0; i < 5; i++) {
            //Process process = processes.remove(0);
            /*
            InputStreamReader inputStreamReader = new InputStreamReader(
                    process.getInputStream()
            );
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader
            );
            try {
                System.out.println(bufferedReader.readLine());
            }catch (IOException ioe)
            {
                ioe.printStackTrace();
            }*/
        }
    }
}
