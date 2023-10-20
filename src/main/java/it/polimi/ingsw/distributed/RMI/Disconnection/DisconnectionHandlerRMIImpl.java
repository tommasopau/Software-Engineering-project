package it.polimi.ingsw.distributed.RMI.Disconnection;

import it.polimi.ingsw.view.View;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class used for checking if an RMI client has disconnected from the game
 */
public class DisconnectionHandlerRMIImpl extends UnicastRemoteObject implements DisconnectionHandlerRMI {

    View view;

    /**
     * Constructor method
     * @param view the view
     * @throws RemoteException related to RMI
     */
   public DisconnectionHandlerRMIImpl(View view) throws RemoteException {
       this.view = view;
    }

    /**
     * Method that wait a disconnection and throws a RemoteException
     */
    public void waitDisconnectionLoop(){

    }

    /**
     * Printing that a player has disconnected
     */
    public void playerHasDisconnected()
    {
        view.printDisconnection(1);
        System.exit(1);
    }
}
