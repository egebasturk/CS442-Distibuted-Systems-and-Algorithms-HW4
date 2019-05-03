import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server implements RemoteInterface {
    private ArrayList<String> toBeMatchedList;
    public Server(){
        super();
        toBeMatchedList = new ArrayList<>();
    }
    public String match(String name, int timeoutSecs)
    {
        return null;
    }
    public static void main(String args[])
    {
        Server server = new Server();
        try {

            Server stub = (Server) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Server", stub);
        }catch (RemoteException re){
            re.printStackTrace();
        }
    }
}
