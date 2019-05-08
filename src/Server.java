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
    final private Object listLock = new Object();
    final private Object flagLock = new Object();
    private Registry myRegistry;
    boolean flag;

    public Server() throws RemoteException
    {
        //toBeMatchedList = new ArrayList<String>();
        toBeMatchedList = new LinkedList<String>();
        clients = new HashMap<String , String>();
        myRegistry = null;
        flag = false;
    }
    private String acquirePartner(String name, int timeoutSecs, long start)
    {
        long elapsedTime;
        while (true) {
            synchronized (listLock) {
                if (toBeMatchedList.size() > 1) {
                    String partner1 = toBeMatchedList.get(0);
                    String partner2 = toBeMatchedList.get(0);
                    synchronized (flagLock) {
                        if (flag)
                        {
                            toBeMatchedList.remove(0);
                            toBeMatchedList.remove(0);
                            flag = false;
                        }
                        else
                            flag = true;
                    }
                    if (name.equals(partner1))
                        return partner2;
                    else if (name.equals(partner2))
                        return partner1;
                    notifyAll();
                } else {
                    elapsedTime = (System.nanoTime() - start) / (int) Math.pow(10, 9);
                    try {
                        wait(timeoutSecs - elapsedTime);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();

                    }
                    elapsedTime = (System.nanoTime() - start) / (int) Math.pow(10, 9);
                    if (elapsedTime >= timeoutSecs)
                        return null;
                }
            }
        }
    }
    @Override
    public String match(String name, int timeoutSecs)
    {
        long start = System.nanoTime();
        synchronized (listLock) {
            toBeMatchedList.add(name);
        }
        return acquirePartner(name, timeoutSecs, start);


        //String partner1 = "NON", partner2 = "NON";
        //return null;
        /*while (elapsedTime < timeoutSecs)
        {
            elapsedTime = (System.nanoTime() - start) / (int)Math.pow(10,9);
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
        synchronized (listLock){
            toBeMatchedList.remove(name);
            try {
                myRegistry.unbind(name);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;*/
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
        synchronized (listLock) {
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
