package org.projetofsd;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DirectNotificationInterface extends Remote {
    String notifyStockUpdate(String message) throws RemoteException;
}
