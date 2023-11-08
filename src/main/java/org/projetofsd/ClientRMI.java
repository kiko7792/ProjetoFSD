package org.projetofsd;

import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;
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

            Hashtable<String, StockInfo> stockList = stock.getStockRMI();
            stock.readStockCSVRMI("Stock.csv");
            for (Enumeration<String> keys = stockList.keys(); keys.hasMoreElements(); ) {
                String key = keys.nextElement();
                StockInfo stockInfo = stockList.get(key);

              String stock_response = "\nNome do Produto: " + stockInfo.getName() + "\nIdentificador: " + stockInfo.getIdentifier() +
                        "\nQuantidade em stock: " + stockInfo.getQuantity() + "\n---------------------------";
              System.out.println(stock_response);
            }
        } catch (Exception e) {
            System.err.println("Error");
            e.printStackTrace();
        }
    }
}
