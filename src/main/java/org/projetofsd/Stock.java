package org.projetofsd;

import java.util.*;


public class Stock {

    private static Hashtable<String, StockInfo> presentStock = new Hashtable<>();
    private static int cont = 0;
    public Hashtable<String, StockInfo> getStock() {

        cont = cont+1;

        System.out.println("cont = "+ cont);

        synchronized(this) {
            StockInfo banana = new StockInfo("banana", "123", 40);
            presentStock.put("Banana" ,banana);
            StockInfo maça = new StockInfo("maça", "345", 20);
            presentStock.put("Maça" ,maça);
        }

        return presentStock;

    }
    // Método para listar todos os itens em estoque
    public Object listStockItems() {
        for (StockInfo info : presentStock.values()) {
            System.out.println("Nome do Produto: " + info.getName());
            System.out.println("Identificador: " + info.getIdentifier());
            System.out.println("Quantidade em stock: " + info.getQuantity());
            System.out.println("---------------------------");
        }
        return null;
    }

    /*private Vector<String> getStockList(){
        Vector<String> result = new Vector<String>();
        for (Enumeration<StockInfo> e = presentStock.elements(); e.hasMoreElements(); ) {
            StockInfo element = e.nextElement();
            if (!element.timeOutPassed(180*1000)) {
                result.add(element.getNome());
            }
        }
        return result;
    }*/

}

class StockInfo {

    private String name;
    private String identifier;
    private int quantity;
    private long lastSeen;

    public StockInfo(String name, String identifier, int quantity) {
        this.name = name;
        this.identifier = identifier;
        this.quantity = quantity;
    }

   /* public StockInfo(String id, long lastSeen) {
        this.id = id;
        this.lastSeen = lastSeen;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setId(String identifier) {
        this.identifier = identifier;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setLastSeen(long time){
        this.lastSeen = time;
    }

    public boolean timeOutPassed(int timeout){
        boolean result = false;
        long timePassedSinceLastSeen = new Date().getTime() - this.lastSeen;
        if (timePassedSinceLastSeen >= timeout)
            result = true;
        return result;
    }





}