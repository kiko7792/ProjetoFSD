package org.projetofsd;

import java.io.*;
import java.util.*;


public class Stock {

    private static Hashtable<String, StockInfo> presentStock = new Hashtable<>();
    private static int cont = 0;

    public Hashtable<String, StockInfo> getStock() {

        cont = cont + 1;

        System.out.println("cont = " + cont);

        return presentStock;

    }

    // Método para listar todos os itens em estoque
    public String listStockItems() {
        for (StockInfo info : presentStock.values()) {
            System.out.println("Nome do Produto: " + info.getName() + "\nIdentificador: " + info.getIdentifier() +
                    "\nQuantidade em stock: " + info.getQuantity() + "\n---------------------------");
        }
        return "";
    }

   /* public void saveStockCSV(String nomeArquivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            // Escreve o cabeçalho do CSV, se necessário
            writer.println("Nome do Produto,Identificador,Quantidade");

            // Itera sobre os dados do stock e escreve cada linha no CSV
            for (Enumeration<String> keys = presentStock.keys(); keys.hasMoreElements(); ) {
                String key = keys.nextElement();
                StockInfo stockInfo = presentStock.get(key);

                writer.println(stockInfo.getName() + "," + stockInfo.getIdentifier() + "," + stockInfo.getQuantity());
            }

            System.out.println("Stock salvo em " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o stock em CSV: " + e.getMessage());
        }
    }*/
   // Method to save stock data to the CSV file
   public void saveStockCSV(String nomeArquivo) {
       synchronized (this) {
           try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
               writer.println("Nome do Produto,Identificador,Quantidade");

               for (StockInfo stockInfo : presentStock.values()) {
                   writer.println(stockInfo.getName() + "," + stockInfo.getIdentifier() + "," + stockInfo.getQuantity());
               }

               System.out.println("Stock salvo em " + nomeArquivo);
           } catch (IOException e) {
               System.err.println("Erro ao salvar o stock em CSV: " + e.getMessage());
           }
       }
   }

    public void readStockCSV(String nomeArquivo) {

        synchronized (this){
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            // Salta a primeira linha, que é o cabeçalho do CSV
            String linha;
            reader.readLine();

            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(",");
                if (partes.length == 3) {
                    String nome = partes[0];
                    String identificador = partes[1];
                    int quantidade = Integer.parseInt(partes[2]);

                    // Atualize o estoque com os dados lidos
                    StockInfo stockInfo = new StockInfo(nome, identificador, quantidade);
                    presentStock.put(identificador, stockInfo);
                }
            }

            System.out.println("Stock lido de " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao ler o stock de CSV: " + e.getMessage());
        }
    }
    }

    public boolean updateStock(String productIdentifier, int quantityChange) {

        synchronized (this) {
            StockInfo stockInfo = null;

            for (StockInfo info : presentStock.values()) {
                if (info.getIdentifier().equals(productIdentifier)) {
                    stockInfo = info;
                    break;
                }
            }

            if (stockInfo != null) {
                stockInfo.updateQty(quantityChange);
                saveStockCSV("Stock.csv");
                return true; // Stock updated successfully
            }
        }
        return false; // Product not found
    }







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

    public void updateQty(int quantityDelta) {
        // Atualize a quantidade no estoque com o valor delta
        this.quantity += quantityDelta;

        // Verifique se a quantidade não fica negativa
        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }




}