import java.rmi.Remote;

public interface RemoteInterface extends Remote {
    String match(String name, int timeoutSecs);
}
