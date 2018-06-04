package it.polimi.se2018;

import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.network.server.Server;
import it.polimi.se2018.view.cli.CliParser;

public class SagradaLauncher {


    public static void main(String[] args) {
        System.out.println("Benvenuto su Sagrada.");
        System.out.print("Digita 0 per avviare il server, uno per il client: ");
        CliParser cliParser = new CliParser();
        int choice = cliParser.parseInt(1);
        for (int i = 0; i < args.length; i++) System.err.println(args[i]);

        switch (choice) {
            case 0:
                String[] args1 = {};
                Server.main(args1);
                break;
            case 1:
                String[] args2 = new String[1];
                System.out.print("Digita 0 per avviare il cli, uno per la gui: ");

                switch (cliParser.parseInt(1)) {
                    case 0:
                        args2[0] = "cli";
                        break;
                    case 1:
                        args2[0] = "gui";
                        break;
                }

                Client.main(args2);
        }
    }
}