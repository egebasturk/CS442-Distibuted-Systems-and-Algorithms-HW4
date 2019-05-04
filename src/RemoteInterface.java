import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {
    String sName = "Server";
    int serverPortNumber = 6666;
    String match(String name, int timeoutSecs) throws RemoteException;
    String  PrintHello(String name) throws RemoteException;
}
