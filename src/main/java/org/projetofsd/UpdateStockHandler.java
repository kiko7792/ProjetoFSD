package org.projetofsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.Serializable;

public class UpdateStockHandler extends Thread implements Serializable{
    Socket socket;
    Stock stock;
    BufferedReader in;
    PrintWriter out;
    String request;

    public UpdateStockHandler(Socket socket, Stock stock, String request) {
        this.socket = socket;
        this.stock = stock;
        this.request = request;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Erro na execução do servidor: " + e);
            System.exit(1);
        }
    }

    public synchronized void run() {
        synchronized (this) {
            try {
                System.out.println("Aceitou ligação de cliente no endereço " + socket.getInetAddress() + " na porta " + socket.getPort());


                stock.readStockCSV("Stock.csv");
                String stock_response = "";
                String msg = request;
                StringTokenizer tokens = new StringTokenizer(msg);
                String metodo = tokens.nextToken();
                Hashtable<String, Stock.StockInfo> stockList = stock.getStock();
                if (metodo.equals("STOCK_UPDATE")) {
                    String productIdentifier = tokens.nextToken();
                    int quantityChange = Integer.parseInt(tokens.nextToken());

                    // Add or remove stock from the inventory
                    int success = stock.updateStock(productIdentifier, quantityChange);

                    if (success == 1) {
                        stock.saveStockCSV("Stock.csv");
                        out.println("STOCK_UPDATED");
                        out.flush();

                        for (Enumeration<String> keys = stockList.keys(); keys.hasMoreElements(); ) {
                            String key = keys.nextElement();
                            Stock.StockInfo stockInfo = stockList.get(key);

                            stock_response += "\nNome do Produto: " + stockInfo.getName() + "\nIdentificador: " + stockInfo.getIdentifier() +
                                    "\nQuantidade em stock: " + stockInfo.getQuantity() + "\n---------------------------";
                        }
                        System.out.println(stock_response);
                    } else if(success == -1) {
                        out.println("STOCK_ERROR: Excedente de quantidade.");
                    } else if (success == 0) {
                        out.println("STOCK ERROR: Produto não encontrado");
                    }


                }


                out.flush();
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("STOCK_ERROR:" + e);
                System.exit(1);
            }
        }
    }
}
