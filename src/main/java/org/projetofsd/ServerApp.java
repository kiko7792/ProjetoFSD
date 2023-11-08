package org.projetofsd;

import java.rmi.RemoteException;

public class ServerApp {
    public static void main(String[] args) throws RemoteException {

        StockServer ps = new StockServer();
        ps.createStock();
    }
}
