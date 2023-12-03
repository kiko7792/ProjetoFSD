package org.projetofsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.*;
import java.util.Base64;
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

    private final PrivateKey privateKey;

    public UpdateStockHandler(Socket socket, Stock stock, String request, PrivateKey privateKey) {
        this.socket = socket;
        this.stock = stock;
        this.request = request;
        this.privateKey = privateKey;
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
                String stockResponse = "";
                String msg = request;
                StringTokenizer tokens = new StringTokenizer(msg);
                String metodo = tokens.nextToken();
                Hashtable<String, Stock.StockInfo> stockList = stock.getStock();
                if (metodo.equals("STOCK_UPDATE")) {
                    String productIdentifier = tokens.nextToken();
                    int quantityChange = Integer.parseInt(tokens.nextToken());

                    // Add or remove stock from the inventory
                    int success = stock.updateStock(productIdentifier, quantityChange);
                    StringBuilder stockResponseBuilder = new StringBuilder();
                    if (success == 1) {
                        stock.saveStockCSV("Stock.csv");
                        out.println("STOCK_UPDATED");
                        out.flush();

                        for (Enumeration<String> keys = stockList.keys(); keys.hasMoreElements(); ) {
                            String key = keys.nextElement();
                            Stock.StockInfo stockInfo = stockList.get(key);

                            stockResponseBuilder.append("Nome: ").append(stockInfo.getName()).append(", Identificador: ").append(stockInfo.getIdentifier()).append(", Quantidade: ").append(stockInfo.getQuantity()).append("\n");
                            stockResponse = stockResponseBuilder.toString();
                        }
                        System.out.println(stockResponse);
                    } else if(success == -1) {
                        out.println("STOCK_ERROR: Excedente de quantidade.");
                    } else if (success == 0) {
                        out.println("STOCK ERROR: Produto não encontrado");
                    }

                }
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initSign(privateKey);
                byte[] stock = stockResponse.getBytes();
                signature.update(stock);
                byte[] digitalSignature = signature.sign();


                String digitalSignaturString = Base64.getEncoder().encodeToString(digitalSignature);

                String stockWithSignature = stockResponse +"SIGNATURE:"+ digitalSignaturString;

                out.println(stockWithSignature);


                out.flush();
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("STOCK_ERROR:" + e);
                System.exit(1);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
