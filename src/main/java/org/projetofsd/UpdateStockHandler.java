package org.projetofsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.*;

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
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

    public void run(){
        try{
            System.out.println("Ligação aceite no endereço" + ligacao.getInetAddress());

            stock.readStockCSV("Stock.csv");
            String response = "";
            Hashtable<String, StockInfo> stockList = stock.getStock();

            for (Enumeration<String> keys = stockList.keys(); keys.hasMoreElements(); ) {
                String key = keys.nextElement();
                StockInfo stockInfo = stockList.get(key);

                response += "\nNome do Produto: " + stockInfo.getName() + "\nIdentificador: " + stockInfo.getIdentifier() +
                        "\nQuantidade em stock: " + stockInfo.getQuantity() + "\n---------------------------";
            }

            System.out.println(response);
            out.println(response);

            out.println("Digite o nome do artigo para alterar a quantidade:");

            String nomeArtigo = in.readLine();

            if (stockList.containsKey(nomeArtigo)) {
                // Solicitar a quantidade a ser adicionada ou removida
                out.println("Digite a quantidade a ser adicionada ou removida:");

                int quantity = Integer.parseInt(in.readLine()); // Ler a quantidade do usuário


            }
        }catch (IOException e) {
            System.out.println("Erro na execução do servidor: " + e);
            System.exit(1);
        }






        }








}