package org.projetofsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class UpdateStockHandler extends Thread {
    Socket ligacao;
    Stock stock;
    BufferedReader in;
    PrintWriter out;

    public UpdateStockHandler(Socket ligacao, Stock stock) {
        this.ligacao = ligacao;
        this.stock = stock;
        try {
            this.in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
            this.out = new PrintWriter(ligacao.getOutputStream());
        } catch (IOException e) {
            System.out.println("Erro na execução do servidor: " + e);
            System.exit(1);
        }
    }

    public synchronized void run() {
        synchronized (this) {
            try {
                System.out.println("Aceitou ligação de cliente no endereço " + ligacao.getInetAddress() + " na porta " + ligacao.getPort());


                stock.readStockCSV("Stock.csv");
                String msg = in.readLine();
                StringTokenizer tokens = new StringTokenizer(msg);
                String metodo = tokens.nextToken();

                if (metodo.equals("update")) {
                    String productIdentifier = tokens.nextToken();
                    int quantityChange = Integer.parseInt(tokens.nextToken());

                    // Add or remove stock from the inventory
                    boolean success = stock.updateStock(productIdentifier, quantityChange);

                    if (success) {
                        stock.saveStockCSV("Stock.csv");
                        out.println("Stock updated successfully.");
                    } else {
                        out.println("Failed to update stock. Product not found.");
                    }
                }

                out.flush();
                in.close();
                out.close();
                ligacao.close();
            } catch (IOException e) {
                System.out.println("Erro na execução do servidor: " + e);
                System.exit(1);
            }
        }
    }
}
