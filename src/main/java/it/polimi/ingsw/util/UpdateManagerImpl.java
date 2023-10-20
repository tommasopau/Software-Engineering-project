package it.polimi.ingsw.util;

import it.polimi.ingsw.distributed.Messages.MessagesType;
import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * The update manager
 */
public class UpdateManagerImpl extends UnicastRemoteObject implements ClientObserver {
    private SocketRMIMessages takeMessage;
    private SocketRMIMessages orderMessage;
    private SocketRMIMessages placeMessage;
    private SocketRMIMessages nickMessage;

    /**
     * creator of UpdateManagerImpl
     * @throws RemoteException related to RMI
     */
    public UpdateManagerImpl() throws RemoteException {
        takeMessage = new SocketRMIMessages(MessagesType.START);
        orderMessage = new SocketRMIMessages(MessagesType.START);
        placeMessage = new SocketRMIMessages(MessagesType.START);
        nickMessage = new SocketRMIMessages(MessagesType.START);
    }

    /**
     * Method that changes the values of one message, depending on the int passed
     * @param message the message
     * @param i the index
     * @return true if the update was successful
     */
    @Override
    public boolean update(SocketRMIMessages message, int i) {
        switch (i) {
            case 0:
                takeMessage.setMessagesType(message.getMessagesType());
                return true;
            case 1:
                orderMessage.setMessagesType(message.getMessagesType());
                return true;
            case 2:
                placeMessage.setMessagesType(message.getMessagesType());
                return true;
            case 3:
                nickMessage.setMessagesType(message.getMessagesType());
                return true;
        }
        return false;
    }

    /**
     * Method called by the client to get the updated message, depending on the int passed
     * @param i the index
     * @return the message
     */
    @Override
    public SocketRMIMessages getUpdate(int i){
        switch (i) {
            case 0:
                return takeMessage;
            case 1:
                return orderMessage;
            case 2:
                return placeMessage;
            case 3:
                return nickMessage;
        }
        return new SocketRMIMessages(MessagesType.START);
    }



}
