package it.polimi.ingsw.util;

import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;


/**
 * The class Observable
 */
public interface Observable {
    /**
     * Method to add an observer
     * @param observer the observer
     */
    void addObserver(ClientObserver observer);

    /**
     * Method to remove an observer
     * @param observer the observer
     */
    void removeObserver(ClientObserver observer);

    /**
     * Method to notify an observer when a change happened
     * @param message the message
     * @param i the observer number
     */
    void notifyObservers(SocketRMIMessages message, int i);
}