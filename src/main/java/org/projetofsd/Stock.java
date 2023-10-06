package org.projetofsd;

import java.util.*;


public class Stock {

    private static Hashtable<String, StockInfo> presentStock = new Hashtable<String, StockInfo>();
    private static int cont = 0;
    public Vector<String> getStock(String identifier) {

        long actualTime = new Date().getTime();
        cont = cont+1;

        System.out.println("cont = "+ cont);

        //Assume-se que o IP e valido!!!!!
        synchronized(this) {
            if (presentStock.containsKey(identifier)) {
                StockInfo newIp = presentStock.get(identifier);
                newIp.setLastSeen(actualTime);
            }
            else {
                StockInfo newStock = new StockInfo(identifier, actualTime);
                presentStock.put(identifier,newStock);
            }
        }
        return getStockList();
    }

    private Vector<String> getStockList(){
        Vector<String> result = new Vector<String>();
        for (Enumeration<StockInfo> e = presentStock.elements(); e.hasMoreElements(); ) {
            StockInfo element = e.nextElement();
            if (!element.timeOutPassed(180*1000)) {
                result.add(element.getId());
            }
        }
        return result;
    }
}

class StockInfo {

    private String nome;
    private String id;
    private int quantity;
    private long lastSeen;

    public StockInfo(String nome, String id, int quantity) {
        this.nome = nome;
        this.id = id;
        this.quantity = quantity;
    }

    public StockInfo(String id, long lastSeen) {
        this.id = id;
        this.lastSeen = lastSeen;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    String identifier = "banana"; // O identificador que você deseja usar para a banana
    StockInfo banana = new StockInfo(identifier, 3); // Crie um objeto StockInfo para a banana


}
