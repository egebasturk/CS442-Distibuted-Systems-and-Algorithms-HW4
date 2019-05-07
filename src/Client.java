import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class Client extends UnicastRemoteObject implements ClientInterface
{
    private Client partnerHandle;
    private String myName;
    private Registry myRegistry;
    public Client() throws RemoteException
    {
        partnerHandle = null;
        myName = null;
        myRegistry = null;
    }
    @Override
    public void bindMe() throws AlreadyBoundException, AccessException, RemoteException
    {
        myRegistry.bind(myName, this);
    }
    @Override
    public void handShake(String partnerName) throws NotBoundException, AccessException, RemoteException
    {
        ClientInterface myPartner = (ClientInterface) myRegistry.lookup(partnerName);
    }
    public static void main(String args[])
    {
        //System.setSecurityManager(new RMISecurityManager());

        try
        {
            Client client = new Client();
            client.myRegistry = LocateRegistry.getRegistry(RemoteInterface.serverPortNumber);
            RemoteInterface server = (RemoteInterface) client.myRegistry.lookup(RemoteInterface.sName);

            client.myName = UUID.randomUUID().toString();
            System.out.println(server.PrintHello(client.myName));

            client.bindMe();

            String result = server.match(client.myName, 5);
            System.out.println("My[" + client.myName + "] Result: " + result);
            if (result != null)
            {
                client.handShake(result);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void getPartnerHandle(Client partner) {
        partnerHandle = partner;
    }
}
