package org.projetofsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.*;


public class GetStockRequestHandler {
    Socket ligacao;
    Stock stock;
    BufferedReader in;
    PrintWriter out;

    public GetStockRequestHandler(Socket ligacao, Stock stock) {
        this.ligacao = ligacao;
        this.stock = stock;
        try {
            this.in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));

            this.out = new PrintWriter(ligacao.getOutputStream());
        } catch (IOException e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

    public void run() {
        try {
            System.out.println("Aceitou ligacao de cliente no endereco " + ligacao.getInetAddress() + " na porta " + ligacao.getPort());

            StringBuilder response;
            response = new StringBuilder("101\n");
            Hashtable<String, StockInfo> stockList = stock.getStock();
            response.append(stockList.size()).append("\n");

            for (Enumeration<String> keys = stockList.keys(); keys.hasMoreElements(); ) {
                String key = keys.nextElement();
                StockInfo stockInfo = stockList.get(key);

                response.append(stock.listStockItems());
            }

            System.out.println(response);
            out.println(response);

            out.flush();
            in.close();
            out.close();
            ligacao.close();

        } catch (IOException e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }
}



