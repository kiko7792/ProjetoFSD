package org.projetofsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.*;


public class GetStockRequestHandler extends Thread {
    Socket socket;
    Stock stock;
    BufferedReader in;
    PrintWriter out;
    String request;


    public GetStockRequestHandler(Socket socket, Stock stock, String request) {
        this.socket = socket;
        this.stock = stock;
        this.request = request;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

    public void run() {
        synchronized (this) {
            try {
                System.out.println("Aceitou ligacao de cliente no endereco " + socket.getInetAddress() + " na porta " + socket.getPort());


                stock.readStockCSV("Stock.csv");
                String stock_response = "";
                String msg = request;

                StringTokenizer tokens = new StringTokenizer(msg);
                String metodo = tokens.nextToken();
                Hashtable<String, StockInfo> stockList = stock.getStock();
                if (metodo.equals("STOCK_REQUEST")) {
                    for (Enumeration<String> keys = stockList.keys(); keys.hasMoreElements(); ) {
                        String key = keys.nextElement();
                        StockInfo stockInfo = stockList.get(key);

                        stock_response += "\nNome do Produto: " + stockInfo.getName() + "\nIdentificador: " + stockInfo.getIdentifier() +
                                "\nQuantidade em stock: " + stockInfo.getQuantity() + "\n---------------------------";
                    }
                }

                out.println(stock_response);

                out.flush();
                in.close();
                out.close();
                socket.close();

            } catch (IOException e) {
                System.out.println("STOCK_ERROR: " + e);
                System.exit(1);
            }

        }
    }
}



