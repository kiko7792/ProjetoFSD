package org.projetofsd;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DirectNotificationImpl extends UnicastRemoteObject implements DirectNotificationInterface {

    public DirectNotificationImpl() throws RemoteException{

    }
    @Override
    public void notifyStockUpdate(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public void stockUpdatedSigned(String message, String signature) throws RemoteException{
        System.out.println(message + signature);
    }
}
