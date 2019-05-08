import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Server extends UnicastRemoteObject implements RemoteInterface
{
    private List<String>  toBeMatchedList;
    private Queue<String> queue;
    private HashMap<String , String > clients;
    final private Object listLock = new Object();
    final private Object flagLock = new Object();
    private Registry myRegistry;
    private boolean interruptFlag = false;

    String partner1;
    String partner2;


    public Server() throws RemoteException
    {
        toBeMatchedList = new LinkedList<String>();
        //queue = new ConcurrentLinkedQueue<String>();
        queue = new LinkedList<String>();
        myRegistry = null;

    }
    @Override
    public String match(String name, int timeoutSecs)
    {
        long start = System.nanoTime();
        int timeOutMillis = timeoutSecs * 1000;
        long elapsedTime;

        MyRunnable myRunnable = new MyRunnable(name, timeoutSecs, start, queue);
        Thread thread = new Thread(myRunnable);
        thread.start();
        try {
            thread.join();
        }catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }

        String p1 = myRunnable.getPartner1();
        String p2 = myRunnable.getPartner2();
        if (p1 == null && p2 != null)
            return p2;
        else if (p1 != null && p2 == null)
            return p1;
        else
            return null;
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
            server.myRegistry = LocateRegistry.createRegistry(serverPortNumber);

            server.myRegistry.rebind(sName, server);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public class MyRunnable implements Runnable
    {
        String name;
        String partner1;
        String partner2;
        Queue<String > queue;
        long start;
        int timeoutSecs;
        MyRunnable(String name, int timeoutSecs, long start, Queue queue)
        {
            this.name = name;
            this.timeoutSecs = timeoutSecs;
            this.start = start;
            this.queue = queue;
            partner1 = null;
            partner2 = null;
        }

        public String getPartner1() {
            String tmp = partner1;
            partner1 = null;
            return tmp;
        }
        public String getPartner2() {
            String tmp = partner2;
            partner2 = null;
            return tmp;
        }

        @Override
        public void run() {
            long start = System.nanoTime();
            int timeOutMillis = timeoutSecs * 1000;
            long elapsedTime;
            synchronized (listLock) {
                queue.add(name);
                while (queue.size() < 2) {
                    try {
                        elapsedTime = (System.nanoTime() - start) / (int) Math.pow(10, 6);

                        if (elapsedTime >= timeOutMillis) {
                            queue.poll();
                            return;
                        }
                        listLock.wait(timeOutMillis - elapsedTime);

                        if (interruptFlag) {
                            partner2 = queue.poll();
                            queue.poll();
                            interruptFlag = false;
                            return;
                        }
                    } catch (InterruptedException ie) {
                        if (interruptFlag) {
                            partner2 = queue.poll();
                            interruptFlag = false;
                            System.out.println("INTERRUPTED");
                            return;
                        }
                    }
                }
                partner1 = queue.poll();
                queue.add(name);
                interruptFlag = true;
                listLock.notifyAll();
            }
        }
    }
}
