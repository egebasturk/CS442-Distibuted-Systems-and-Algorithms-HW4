/**
 * @author Alp Ege Basturk
 * RMI Server Implementation
 * */

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
    final private Object listLock = new Object(); // Main sync. lock
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
    /**
     * Initiates timer and creates a thread to handle the call.
     * returns value depending on the result obtained from the thread.
     * Threads are synchronized among themselves using wait/notify
     * A thread waits until timeout or another thread wakes it up via notify
     * */
    @Override
    public String match(String name, int timeoutSecs)
    {
        long start = System.nanoTime();

        // Create Thread
        MyRunnable myRunnable = new MyRunnable(name, timeoutSecs, start, queue);
        Thread thread = new Thread(myRunnable);
        thread.start();
        try {
            // Synchronize
            thread.join();
        }catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }

        // Get results
        // Following methods also reset values which they return
        String p1 = myRunnable.getPartner1();
        String p2 = myRunnable.getPartner2();
        if (p1 == null && p2 != null)
            return p2;
        else if (p1 != null && p2 == null)
            return p1;
        else
            return null;
    }
    /**
     * Debug to check clients connection with the server
     * */
    @Override
    public String PrintHello(String name) throws RemoteException {
        System.out.println("Hello " + name);
        return "Hello from Server to " + name;
    }

    /**
     * Main function which init.s a server object.
     * */
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

    /**
     * Custom thread class
     * */
    public class MyRunnable implements Runnable
    {
        // Variables
        String name;
        String partner1;
        String partner2;
        Queue<String > queue;
        long start;
        int timeoutSecs;
        // Default constructor must get the arguments during creation
        MyRunnable(String name, int timeoutSecs, long start, Queue queue)
        {
            this.name = name;
            this.timeoutSecs = timeoutSecs;
            this.start = start;
            this.queue = queue;
            partner1 = null;
            partner2 = null;
        }
        /**
         * Getter methods. Both reset the values which they return
         * */
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

        /**
         * Initially callers add themselves to the queue.
         * If there is a single element in the queue, wait is called in the loop
         * It either times out, which is checked by the if condition with elapsed time
         * or another thread adds one more element to the queue and wakes the sleeping
         * thread up.
         * */
        @Override
        public void run() {
            long start = System.nanoTime();
            int timeOutMillis = timeoutSecs * 1000;
            long elapsedTime;

            /// Lock the code block.
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
