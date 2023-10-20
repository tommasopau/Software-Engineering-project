package it.polimi.ingsw.distributed.RMI.Disconnection;


import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface used for checking if an RMI client has disconnected from the game
 */
public interface DisconnectionHandlerRMI extends Remote {

    /**
     * Method that wait a disconnection and throws a RemoteException
     * @throws InterruptedException related to RMI
     * @throws RemoteException related to RMI
     */
    void waitDisconnectionLoop() throws InterruptedException, RemoteException;

    /**
     * Printing that a player has disconnected
     * @throws RemoteException related to RMI
     */
    void playerHasDisconnected() throws RemoteException;
}
