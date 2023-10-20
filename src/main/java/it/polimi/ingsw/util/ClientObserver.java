package it.polimi.ingsw.util;

import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Observers for client
 */
public interface ClientObserver extends Remote{
    /**
     * Method that updates one of the messages in the observer depending on the int passed
     * @param message message of the observer
     * @param i observer number
     * @return true if positive, false if not working
     * @throws RemoteException related to RMI
     */
    boolean update(SocketRMIMessages message, int i) throws RemoteException;

    /**
     * Method to get the message linked to the integer i from the observer
     * @param i observer number
     * @return the message
     * @throws RemoteException related to RMI
     */
    SocketRMIMessages getUpdate(int i) throws RemoteException;
}