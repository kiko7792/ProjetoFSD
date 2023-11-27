package org.projetofsd;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.sql.SQLOutput;
import java.util.Scanner;


public class ClientRMI {

    String SERVICE_NAME="/StockServer";
    String[] args;

    public ClientRMI(String[] args)  {
        this.args = args;
    }
    public void putPresence() {
        if (args.length != 2) {
            System.out.println("Erro: use java ClientApp <ipClient> <ipNameServer>");
            System.exit(-1);
        }

        try {
            DirectNotificationInterface directNotifications = new DirectNotificationImpl();


            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));

            StockServerInterface stockServer = (StockServerInterface) registry.lookup(SERVICE_NAME);
            PublicKey serverPublicKey = stockServer.getPubKey();
            stockServer.subscribe(directNotifications);
            System.out.println("Client: "+ serverPublicKey);
            System.out.println("Aceitou ligacao de cliente no endereco " + args[0] + " na porta " + args[1]);


            System.out.println("\n************************************************************");
            System.out.println("*	               Conectado com Sucesso                  *");
            System.out.println("************************************************************   ");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n************************************************************");
                System.out.println("*	                Menu Cliente RMI                      *");
                System.out.println("************************************************************   ");
                System.out.println("*	1 - Listar Stock ");
                System.out.println("*	2 - Adicionar/Remover Stock ");
                System.out.println("*	3 - Sair      ");
                System.out.print("Selecione uma opção(1-3): ");
                int escolha = scanner.nextInt();


                if (escolha == 1) {
                    // Listar o stock
                    String stock = stockServer.stock_request();
                    System.out.println("Stock:");
                    System.out.println(stock);
                } else if (escolha == 2) {
                    // Atualizar a quantidade de um item no stock
                    String stock = stockServer.stock_request();
                    System.out.println(stock);
                    System.out.print("Introduza o identificador do produto: ");
                    String productIdentifier = scanner.next();
                    System.out.print("Introduza a quantidade a adicionar (nªpositivo) ou a remover (nªnegativo): ");
                    int quantityChange = scanner.nextInt();
                    String update = stockServer.stock_update(productIdentifier, quantityChange);
                    System.out.println(update);


                } else if (escolha == 3) {
                    // Sair do loop e encerrar o cliente
                    break;
                } else {
                    System.out.println("Escolha inválida. Tente novamente.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
