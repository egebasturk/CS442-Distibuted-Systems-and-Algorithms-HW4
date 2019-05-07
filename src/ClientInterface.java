import java.rmi.*;

public interface ClientInterface extends Remote {
    void getPartnerHandle(Client partner) throws RemoteException;
    void bindMe() throws AlreadyBoundException, AccessException, RemoteException;
    void handShake(String partnerName)  throws NotBoundException, AccessException, RemoteException;
    void sendMessage(String message) throws RemoteException;
}
