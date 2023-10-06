package org.projetofsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class GetStockRequestHandler {
    Socket ligacao;
    Stock stock;
    BufferedReader in;
    PrintWriter out;

    public GetStockRequestHandler(Socket ligacao, Stock stock) {
        this.ligacao = ligacao;
        this.stock = stock;
        try
        {
            this.in = new BufferedReader (new InputStreamReader(ligacao.getInputStream()));

            this.out = new PrintWriter(ligacao.getOutputStream());
        } catch (IOException e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

    public void run() {
        try {
            System.out.println("Aceitou ligacao de cliente no endereco " + ligacao.getInetAddress() + " na porta " + ligacao.getPort());

            String response;
            String msg = in.readLine();
            System.out.println("Request=" + msg);

            StringTokenizer tokens = new StringTokenizer(msg);
            String metodo = tokens.nextToken();
            if (metodo.equals("get")) {
                response = "101\n";
                String id = tokens.nextToken();
                Vector<String> stockList = stock.getStock(id);
                response += stockList.size() + "\n";
                for (Iterator<String> it = stockList.iterator(); it.hasNext();){
                    String next = it.next();
                    response += next + ";";
                }
                System.out.println(response);
                out.println(response);
            }
            else
                out.println("201;method not found");

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

