package org.projetofsd;

import java.io.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.RemoteException;

public class Stock extends UnicastRemoteObject{

    public static Hashtable<String, StockInfo> presentStock = new Hashtable<>();

    public Hashtable<String, StockInfo> getStock() {

        return presentStock;

    }

    public Stock() throws RemoteException{
        super();
    }

    //Metodo para salvar no csv
    public void saveStockCSV(String filename) {
        synchronized (this) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("Nome do Produto,Identificador,Quantidade");

                for (StockInfo stockInfo : presentStock.values()) {
                    writer.println(stockInfo.getName() + "," + stockInfo.getIdentifier() + "," + stockInfo.getQuantity());
                }

            } catch (IOException e) {
                System.err.println("Erro ao salvar o stock em CSV: " + e.getMessage());
            }
        }
    }

    public void readStockCSV(String filename) {

        synchronized (this){
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                // Salta a primeira linha, que é o cabeçalho do CSV
                String linha;
                reader.readLine();

                while ((linha = reader.readLine()) != null) {
                    String[] partes = linha.split(",");
                    if (partes.length == 3) {
                        String nome = partes[0];
                        String identificador = partes[1];
                        int quantidade = Integer.parseInt(partes[2]);

                        // Atualiza o stock com os dados lidos
                        StockInfo stockInfo = new StockInfo(nome, identificador, quantidade);
                        presentStock.put(identificador, stockInfo);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler o stock de CSV: " + e.getMessage());
            }
        }
    }

    public int updateStock(String productIdentifier, int quantityChange) {

        synchronized (this) {
            StockInfo stockInfo = null;

            for (StockInfo info : presentStock.values()) {
                if (info.getIdentifier().equals(productIdentifier)) {
                    stockInfo = info;
                    break;
                }
            }
            int a = stockInfo.updateQty(quantityChange);
            
            if (stockInfo != null && a == 1) {
                saveStockCSV("Stock.csv");
                return 1; // Stock updated successfully
            } else if (a == -1 || a == 0) {
                 return -1;
            }
        }
        return 0; // Product not found
    }

    static class StockInfo {

        private String name;
        private String identifier;
        private int quantity;

        public StockInfo(String name, String identifier, int quantity) {
            this.name = name;
            this.identifier = identifier;
            this.quantity = quantity;
        }

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


        public int updateQty(int quantity) {
            // Atualize a quantidade
            this.quantity += quantity;

            // Verificar se a quantidade não fica negativa
            if (this.quantity < 0) {
                this.quantity -= quantity;

                return 0;
            } else if (this.quantity > 250) {
                this.quantity -= quantity;

                return -1;
            }
            return 1;
        }

    }
}