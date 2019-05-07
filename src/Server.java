import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Server extends UnicastRemoteObject implements RemoteInterface
{
    //private ArrayList<String> toBeMatchedList;
    private List<String>  toBeMatchedList;
    private HashMap<String , String > clients;
    final private Integer ListLock = new Integer(1);
    private Registry myRegistry;

    public Server() throws RemoteException
    {
        //toBeMatchedList = new ArrayList<String>();
        toBeMatchedList = new LinkedList<String>();
        clients = new HashMap<String , String>();
        myRegistry = null;
    }
    @Override
    public String match(String name, int timeoutSecs)
    {
        long start = System.nanoTime();
        long elapsedTime = (System.nanoTime() - start) / (int)Math.pow(10,9);
        toBeMatchedList.add(name);

        String partner1 = "NON", partner2 = "NON";

        while (elapsedTime < timeoutSecs)
        {
            elapsedTime = (System.nanoTime() - start) / (int)Math.pow(10,9);
            synchronized (ListLock)
            {
                if (toBeMatchedList.size() >= 2)
                {
                    partner1 = toBeMatchedList.remove(0);
                    partner2 = toBeMatchedList.remove(0);

                    if (name.equals(partner1))
                    {
                        return partner2;
                    }
                    else
                    {
                        return partner1;
                    }
                }
            }
        }
        synchronized (ListLock){
            toBeMatchedList.remove(name);
            try {
                myRegistry.unbind(name);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
        /*while (toBeMatchedList.size() < 2)
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
        synchronized (ListLock) {
            String partner2 = toBeMatchedList.remove(1);
            String partner1 = toBeMatchedList.remove(0);
            if (name.equals(partner1)) {
                return partner2;
            } else if (name.equals(partner2)) {
                return partner1;
            }
            System.out.println("Here");
            return "EndNull";
        }*/
    }
    @Override
    public String PrintHello(String name) throws RemoteException {
        System.out.println("Hello " + name);
        return "Hello from Server to " + name;
    }

    public static void main(String args[])
    {
        try {
            Server server = new Server();
            //Server stub = (Server) UnicastRemoteObject.exportObject(server, 0);
            //Registry registry = LocateRegistry.getRegistry();
            server.myRegistry = LocateRegistry.createRegistry(serverPortNumber);

            server.myRegistry.rebind(sName, server);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
