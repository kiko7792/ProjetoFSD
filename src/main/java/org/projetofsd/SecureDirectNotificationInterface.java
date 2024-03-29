package org.projetofsd;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SecureDirectNotificationInterface extends Remote {
    void notifyStockUpdate(String message) throws RemoteException;
    String stockUpdatedSigned(String message, String signature) throws RemoteException;
}
