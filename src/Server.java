import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server extends UnicastRemoteObject implements RemoteInterface
{
    //private ArrayList<String> toBeMatchedList;
    private ArrayList<String>  toBeMatchedList;

    public Server() throws RemoteException
    {
        //toBeMatchedList = new ArrayList<String>();
        toBeMatchedList = new ArrayList<String>();
    }
    @Override
    public String match(String name, int timeoutSecs)
    {
        long start = System.nanoTime();
        //System.out.println(start);
        toBeMatchedList.add(name);

        while (toBeMatchedList.size() < 2)
        {
            long elapsedTime = (System.nanoTime() - start) / (int)Math.pow(10,9);
            //System.out.println(elapsedTime);
            if (elapsedTime >= timeoutSecs)
            {
                System.out.println("Timeout");
                toBeMatchedList.remove(name);
                return "TimeOutNull";
            }
        }
        String partner1 = toBeMatchedList.remove(0);
        String partner2 = toBeMatchedList.remove(0);
        if (name.equals(partner1))
        {
            return "p2";
        }
        else if (name.equals(partner2))
        {
            return "p1";
        }
        System.out.println("Here");
        return "EndNull";
    }
    @Override
    public String PrintHello(String name) throws RemoteException {
        System.out.println("Hello from Server to " + name);
        return "Hello from Server to " + name;
    }

    public static void main(String args[])
    {
        try {
            Server server = new Server();
            //Server stub = (Server) UnicastRemoteObject.exportObject(server, 0);
            //Registry registry = LocateRegistry.getRegistry();
            Registry registry = LocateRegistry.createRegistry(serverPortNumber);

            registry.rebind(sName, server);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
