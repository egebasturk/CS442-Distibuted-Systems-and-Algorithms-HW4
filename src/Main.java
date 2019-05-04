import java.io.File;
import java.io.IOException;

public class Main {

    public static void initClient()
    {
        final String javaHome = "/usr";
        final String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = Client.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className);
        builder.redirectErrorStream();

        try {
            Process process = builder.start();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
	// write your code here
        for (int i = 0; i < 4; i++) {
            initClient();
        }
    }
}
