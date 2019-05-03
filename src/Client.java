import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class Client {
    public static void main(String args[])
    {
        try {
            String sName = "Server";
            Registry registry = LocateRegistry.getRegistry(args[0]);
            Server server = (Server) registry.lookup(sName);

            String myName = UUID.randomUUID().toString();

            String result = server.match(myName, 5);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
