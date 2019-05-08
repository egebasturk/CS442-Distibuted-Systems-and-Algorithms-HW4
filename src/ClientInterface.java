/**
 * @author Alp Ege Basturk
 * RMI Cient Interface
 * */

import java.rmi.*;

public interface ClientInterface extends Remote {
    void bindMe() throws AlreadyBoundException, AccessException, RemoteException;
    void handShake(String partnerName)  throws NotBoundException, AccessException, RemoteException;
    void sendMessage(String message) throws RemoteException;
}
