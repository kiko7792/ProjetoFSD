package org.projetofsd;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DirectNotificationImpl extends UnicastRemoteObject implements DirectNotificationInterface {

    public DirectNotificationImpl() throws RemoteException{

    }
    @Override
    public String notifyStockUpdate(String message) throws RemoteException {
        System.out.println(message);
        return message;
    }
}
