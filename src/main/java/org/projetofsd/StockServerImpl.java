package org.projetofsd;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class StockServerImpl extends UnicastRemoteObject implements StockServerInterface{
    private final List<DirectNotificationInterface> subscribers;
    int n;
    public StockServerImpl() throws RemoteException {
        subscribers = new ArrayList<>();
    }


    @Override
    public void saveStockCSVRMI(String filename) throws RemoteException {
        synchronized (this) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("Nome do Produto,Identificador,Quantidade");

                for (Stock.StockInfo stockInfo : Stock.presentStock.values()) {
                    writer.println(stockInfo.getName() + "," + stockInfo.getIdentifier() + "," + stockInfo.getQuantity());
                }

            } catch (IOException e) {
                System.err.println("Erro ao salvar o stock em CSV: " + e.getMessage());
                throw new RemoteException("Erro ao salvar o stock em CSV", e);
            }
        }
    }
    @Override
    public void readStockCSVRMI(String filename) throws RemoteException {
        synchronized (this) {
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
                        Stock.StockInfo stockInfo = new Stock.StockInfo(nome, identificador, quantidade);
                        Stock.presentStock.put(identificador, stockInfo);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler o stock de CSV: " + e.getMessage());
                throw new RemoteException("Erro ao ler o stock de CSV", e);
            }
        }
    }

    @Override
    public void subscribe(DirectNotificationInterface directNotification) throws RemoteException {
        subscribers.add(directNotification);
    }

    @Override
    public boolean updateStockRMI(String productIdentifier, int quantityChange) throws RemoteException {
        synchronized (this) {
            Stock.StockInfo stockInfo = Stock.presentStock.get(productIdentifier);

            if (stockInfo != null) {
                stockInfo.updateQty(quantityChange);
                saveStockCSVRMI("Stock.csv");
                return true; // Stock updated successfully
            }
        }
        return false; // Product not found
    }
    @Override
    public String stock_request() throws RemoteException {

        Stock stock = new Stock();
        stock.readStockCSV("Stock.csv");

        StringBuilder stockDetails = new StringBuilder();
        stockDetails.append("Lista de Itens no Stock:\n");

        for (Stock.StockInfo stockInfo : Stock.presentStock.values()) {
            stockDetails.append("Nome: ").append(stockInfo.getName()).append(", Identificador: ").append(stockInfo.getIdentifier()).append(", Quantidade: ").append(stockInfo.getQuantity()).append("\n");
        }

        return stockDetails.toString();
    }

    @Override
    public String stock_update(String id, int qty) throws RemoteException {

        boolean success = updateStockRMI(id, qty);

        if(success){
            System.out.println("STOCK_UPDATED");
            String stock = "\nSTOCK_UPDATED\n";
            stock += stock_request();
            notifySubscribers("Stock updated by user: \n" + stock_request());
            return stock;

        }else{
            return "STOCK_ERROR";
        }
    }

    private void notifySubscribers(String message) throws RemoteException {
        for (DirectNotificationInterface directNotification : subscribers) {
            directNotification.notifyStockUpdate(message);
        }
    }


}
