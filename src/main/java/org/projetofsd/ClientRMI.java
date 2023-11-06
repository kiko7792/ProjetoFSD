package org.projetofsd;

import java.rmi.registry.LocateRegistry;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class ClientRMI {

    String SERVICE_NAME="/StockServer";
    String[] args;

    public ClientRMI(String[] args)  {
        this.args = args;
    }
    public void putPresence() {
        if (args.length != 2) {
            System.out.println("Erro: use java ClientApp <ipClient> <ipNameServer>");
            System.exit(-1);
        }

        try {

            StockInterface stock = (StockInterface) LocateRegistry.getRegistry(args[1]).lookup(SERVICE_NAME);

            Hashtable<String, StockInfo> stockList = stock.getStock();

            for (String key : stockList.keySet()) {
                StockInfo stockInfo = stockList.get(key);
                System.out.println("Key: " + key + ", Value: " + stockInfo);
            }
        } catch (Exception e) {
            System.err.println("Error");
            e.printStackTrace();
        }
    }
}
