package it.polimi.ingsw.distributed.Messages;

import java.io.Serializable;

/**
 * Class for the exchange of messages between Client and Server
 */
public class SocketRMIMessages implements Serializable {
    String errorDescription;
    MessagesType messagesType;

    /**
     * constructor that creates the error descriptions linked to the different messages
     * @param messagesType the type of message
     */
    public SocketRMIMessages(MessagesType messagesType)
    {
        this.messagesType = messagesType;
        if(messagesType == MessagesType.NUMBER_OF_PLAYERS_NOT_VALID)
        {
            errorDescription = "Value inserted for number of players not valid, try again.";
        }
        if(messagesType == MessagesType.NICKNAME_FULL_GAME)
        {
            errorDescription = "Game is full";
        }
        if(messagesType == MessagesType.NICKNAME_TAKEN)
        {
            errorDescription = "Nickname is already taken, try again.";
        }
        if(messagesType == MessagesType.NICKNAME_TOO_LONG)
        {
            errorDescription = "Nickname is too long, try again.";
        }
        if(messagesType == MessagesType.NICKNAME_NOT_VALID_CHARACTERS)
        {
            errorDescription = "Nickname must contain at least an alphabet or a number, try again.";
        }
        if(messagesType == MessagesType.NICKNAME_CORRECT)
        {
            errorDescription = "";
        }
        if(messagesType == MessagesType.NUMBER_OF_PLAYERS_NOT_VALID)
        {
            errorDescription = "Value inserted not valid, try again.";
        }
        if(messagesType == MessagesType.NOT_ENOUGH_SPACE_TAKE)
        {
            errorDescription = "There is not a column with enough empty spaces for the number of tiles asked, try again.";
        }
        if(messagesType == MessagesType.NUMBER_OF_TILES_WANTED_NOT_VALID)
        {
            errorDescription = "Number of tiles wanted is not a valid number, try again.";
        }
        if(messagesType == MessagesType.NOT_VALID_CONFIGURATION_TAKE)
        {
            errorDescription = "The tiles you take must be adjacent to each other and form a straight line, try again.";
        }
        if(messagesType == MessagesType.NOT_TAKEABLE_TILE)
        {
            errorDescription = "You have chosen a tile that cannot be taken, try again.";
        }
        if(messagesType == MessagesType.DUPLICATE_TAKE_TILE)
        {
            errorDescription = "You cannot take the same tile for more than one time, try again.";
        }
        if(messagesType == MessagesType.NOT_ENOUGH_TAKEABLE_TILES)
        {
            errorDescription = "There is not a configuration where you can take the number of tiles that you want, try again.";
        }
        if(messagesType == MessagesType.CORRECT_TAKE)
        {
            errorDescription = "";
        }
        if(messagesType == MessagesType.NOT_VALID_ORDER_VALUES)
        {
            errorDescription = "Not valid number inserted, try again";
        }
        if(messagesType == MessagesType.DUPLICATE_ORDER_VALUES)
        {
            errorDescription = "Duplicate values inserted, try again";
        }
        if(messagesType == MessagesType.CORRECT_ORDER_VALUES)
        {
            errorDescription = "";
        }
        if(messagesType == MessagesType.NOT_CORRECT_VALUE_PLACE)
        {
            errorDescription = "Number inserted is not valid, try again.";
        }
        if(messagesType == MessagesType.NOT_ENOUGH_SPACE_IN_COLUMN)
        {
            errorDescription = "The column you asked to place the tiles has not enough spaces, try again.";
        }
        if(messagesType == MessagesType.CORRECT_VALUE_PLACE)
        {
            errorDescription = "";
        }
        if(messagesType == MessagesType.NOT_PLAYER_ON_TURN){
            errorDescription = "It's not your turn";
        }
        if(messagesType == MessagesType.PLAYER_ON_TURN){
            errorDescription = "It's your turn";
        }
        if(messagesType == MessagesType.NEW_GAME_PERSISTENCE)
        {
            errorDescription = "";
        }
        if(messagesType == MessagesType.GAME_ALREADY_CREATED_PERSISTENCE)
        {
            errorDescription = "You are joining a game that was already created. You must insert a nickname that was added before the server crashed";
        }
        if(messagesType == MessagesType.NICKNAME_NOT_FOUND_PERSISTENCE)
        {
            errorDescription = "You inserted a nickname that was not present in the game before the server crashed";
        }
    }

    /**
     * getter for the error description
     * @return errorDescription
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * setter of the message type to change the type of the single message
     * @param messagesType the type of message
     */
    public void setMessagesType(MessagesType messagesType)
    {
        this.messagesType = messagesType;
    }

    /**
     * getter of the message type
     * @return messagesType
     */
    public MessagesType getMessagesType()
    {
        return messagesType;
    }
}
