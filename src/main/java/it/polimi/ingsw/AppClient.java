package it.polimi.ingsw;

import it.polimi.ingsw.distributed.RMI.ClientImpl;
import it.polimi.ingsw.distributed.socket.LineClient;
import it.polimi.ingsw.view.GraphicUI;
import it.polimi.ingsw.view.TextualUI;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * The application to run for running the client
 */
public class AppClient {
    /**
     * main
     * @param args 0: client ip. 1: server ip. 2: server port
     */
    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", args[0]);

        try {
            Integer.parseInt(args[2]);
        }catch (NumberFormatException e)
        {
            System.err.println("You inserted a port that is not a number");
            return;
        }

        Scanner s = new Scanner(System.in);
        boolean correctChoice = false;
        String choiceViewString;
        int choiceView = -1;

        while (correctChoice == false) {
            System.out.println("Choose the interface to use between textual (TUI) and graphic (GUI).");
            System.out.println("\t1. TUI");
            System.out.println("\t2. GUI");
            System.out.println("Your choice (1 - 2):");
            choiceViewString = s.next();

            try {
                choiceView = Integer.parseInt(choiceViewString);
                if (choiceView < 1 || choiceView > 2) {
                    System.err.println("Not a valid number, try again.\n");
                } else {
                    correctChoice = true;
                }
            }
            catch(NumberFormatException e)
            {
                System.err.println("The number you inserted is not an integer, try again.");
            }
        }

        int choiceTech = 0;
        String choiceTechString;
        correctChoice = false;
        while (correctChoice == false) {
            System.out.println("Choose the technology to use between Socket and RMI.");
            System.out.println("\t1. Socket");
            System.out.println("\t2. RMI");
            System.out.println("Your choice:");
            choiceTechString = s.next();

            try {
                choiceTech = Integer.parseInt(choiceTechString);
                if (choiceTech < 1 || choiceTech > 2) {
                    System.err.println("Not a valid number, try again.\n");
                } else {
                    correctChoice = true;
                }
            }
            catch(NumberFormatException e)
            {
                System.err.println("The number you inserted is not an integer, try again.");
            }
        }

        if(choiceTech == 1)
        {
            if(choiceView == 1){
                TextualUI textualUI = new TextualUI();
                startSocket(textualUI, args[1], Integer.parseInt(args[2]));
            }else{
                GraphicUI graphicUI = new GraphicUI();
                startSocket(graphicUI, args[1], Integer.parseInt(args[2]));
            }
        }
        if(choiceTech == 2)
        {
            if(choiceView == 1){
                TextualUI textualUI = new TextualUI();
                startRMI(textualUI, args[1], Integer.parseInt(args[2]));
            }else{
                GraphicUI graphicUI = new GraphicUI();
                startRMI(graphicUI, args[1], Integer.parseInt(args[2]));
            }
        }
    }

    /**
     * Method chosen if the client has chosen socket
     * @param view type of view selected
     * @param ip IP address
     * @param port port chosen
     */
    public static void startSocket(View view, String ip, int port)
    {
        LineClient client = new LineClient(ip, port, view);
        try
        {
            client.startClient();
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Method chosen if the client has chosen socket
     * @param view type of view selected
     * @param ip IP address
     * @param port port chosen
     */
    public static void startRMI(View view, String ip, int port)
    {
        try {
            ClientImpl client = new ClientImpl(ip, port, view);
            client.startClient(port);
        } catch (NotBoundException e) {
            System.err.println("Not Bound Exception: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Interrupted exception: " + e.getMessage());
        } catch (RemoteException e) {
            System.err.println("Remote Exception");
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error while writing on file");
        }
    }
}