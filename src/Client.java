import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class Client
{
    public static void main(String args[])
    {
        //System.setSecurityManager(new RMISecurityManager());

        try
        {
            Registry registry = LocateRegistry.getRegistry(RemoteInterface.serverPortNumber);
            RemoteInterface server = (RemoteInterface) registry.lookup(RemoteInterface.sName);

            String myName = UUID.randomUUID().toString();
            System.out.println(server.PrintHello(myName));

            String result = server.match(myName, 5);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
