package it.polimi.ingsw.distributed.RMI;

import it.polimi.ingsw.distributed.RMI.Disconnection.DisconnectionHandlerRMI;
import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;
import it.polimi.ingsw.model.ItemTile;
import it.polimi.ingsw.util.ClientObserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

/**
 * Server Interface for RMI
 */
public interface Server extends Remote {

    /**
     * Method that prints ip and port of the client
     * @param ip the ip
     * @param port the port
     * @throws RemoteException related to RMI
     */
    void printClientInformation(String ip, int port) throws RemoteException;


    /**
     * Adds a new disconnection handler and starts monitorating the game
     * @param disconnectionHandlerRMI the disconnection handler for RMI
     * @param gameName the game name
     * @throws RemoteException related to RMI
     *
     */
    void askDisconnectionHandler(DisconnectionHandlerRMI disconnectionHandlerRMI, String gameName) throws RemoteException;

    /**
     * Getter for all the game names in the controller
     * @return a list of strings containing the names
     * @throws RemoteException related to RMI
     */
    List<String> getGameNames() throws RemoteException;

    /**
     * Getter for the number of players connected to each controller
     * @return a list of integer containing the numbers
     * @throws RemoteException related to RMI
     */
    List<Integer> getNumberOfPlayersJoined() throws RemoteException;


    /**
     * Getter for the number of players expected in the game
     * @return a list containing the numbers
     * @throws RemoteException related to RMI
     */
    List<Integer> getNumberOfPlayersGame() throws RemoteException;

    /**
     * Method that tries to add a new game
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    void askGameName(String gameName) throws RemoteException;

    /**
     *Checks if a game was already created for persistence
     * @param gameName the game name
     * @return A message with the answer
     * @throws RemoteException related to RMI
     */
    SocketRMIMessages gameWasAlreadyCreatedPersistence(String gameName) throws RemoteException;

    /**
     * Returns true if it is the first player, so the player can insert how many players will play the game
     * @param gameName the game name
     * @return true if it is the first player
     * @throws RemoteException related to RMI
     * @throws InterruptedException related to RMI
     */
    boolean numberOfPlayersNeeded(String gameName) throws RemoteException, InterruptedException;

    /**
     * Client ask to be added to the game. He inserts his nickname and the numberOfPlayer that will play the game
     * @param nickname the nickname
     * @param numberOfPlayers the number of players
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    void addNewPlayer(String nickname, int numberOfPlayers, String gameName) throws RemoteException;

    /**
     * Tries to add a new player to the persistence collection
     * @param nickname the nickname
     * @param gameName the game name
     * @return a message with a positive answer or an error
     * @throws RemoteException related to RMI
     */
    SocketRMIMessages addPlayerGamePersistence(String nickname, String gameName) throws RemoteException;

    /**
     * Method called for waiting that all players join the game
     * @param gameName the game name
     * @throws RemoteException related to RMI
     * @throws InterruptedException related to RMI
     */
    void waitAllPlayers(String gameName) throws RemoteException, InterruptedException;

    /**
     * Returns a list with the description of the commonGoals of the game
     * @param gameName the game name
     * @return a list with the description of the commonGoals of the game
     * @throws RemoteException related to RMI
     */
    List<String> getCommonGoalCardsDescription(String gameName) throws RemoteException;

    /**
     * Returns an ordered list containing the turns of the game (first nickname to last nickname)
     * @param gameName the game name
     * @return an ordered list containing the turns of the game (first nickname to last nickname)
     * @throws RemoteException related to RMI
     */
    List<String> getTurns(String gameName) throws RemoteException;

    /**
     * Returns true if the game has finished
     * @param gameName the game name
     * @return true if the game has finished
     * @throws RemoteException related to RMI
     */
    boolean gameHasFinished(String gameName) throws RemoteException;

    /**
     * Returns true if it is the turn of the player, given a nickname
     * @param nickname the nickname
     * @param gameName the game name
     * @param playerHasPrintedInformation if the player has printed the information
     * @return true if it is the turn of the player
     * @throws RemoteException related to RMI
     * @throws InterruptedException related to RMI
     */
    boolean isPlayerTurn(String nickname, String gameName, boolean playerHasPrintedInformation) throws RemoteException, InterruptedException;

    /**
     * Returns a map, containing for each player their final scores
     * @param gameName the game name
     * @return a map, containing for each player their final scores
     * @throws RemoteException related to RMI
     */
    HashMap<String, Integer> getScores(String gameName) throws RemoteException;

    /**
     * Returns the nickname of the winner
     * @param gameName the game name
     * @return the nickname of the winner
     * @throws RemoteException related to RMI
     */
    String getWinner(String gameName) throws RemoteException;

    /**
     * Returns the number of rows of the bookshelf
     * @return the number of rows of the bookshelf
     * @throws RemoteException related to RMI
     */
    int getNumberOfRowsBookshelf() throws RemoteException;

    /**
     * Returns the number of columns of the bookshelf
     * @return the number of columns of the bookshelf
     * @throws RemoteException related to RMI
     */
    int getNumberOfColumnsBookshelf() throws RemoteException;

    /**
     * Returns a tile of the personal Goal Card of a player, given the position and the nickname
     * @param nickname nickname
     * @param x coordinate x
     * @param y coordinate y
     * @param gameName the game name
     * @return a tile of the personal Goal Card
     * @throws RemoteException related to RMI
     */
    ItemTile getPersonalGoalItemTile(String nickname, int x, int y, String gameName) throws RemoteException;

    /**
     * Returns a tile of the bookshelf of a player, given the position and the nickname
     * @param nickname the nickname
     * @param x coordinate x
     * @param y coordinate y
     * @param gameName the game name
     * @return a tile of the bookshelf
     * @throws RemoteException related to RMI
     */
    ItemTile getBookshelfItemTile(String nickname, int x, int y, String gameName) throws RemoteException;

    /**
     * Returns the number of rows of the board
     * @return the number of rows of the board
     * @throws RemoteException related to RMI
     */
    int getNumberOfRowsBoard() throws RemoteException;

    /**
     * Returns the number of columns of the board
     * @return the number of columns of the board
     * @throws RemoteException related to RMI
     */
    int getNumberOfColumnsBoard() throws RemoteException;

    /**
     * Return a tile of the board, given the position
     * @param x coordinate x
     * @param y coordinate y
     * @param gameName the game name
     * @return a tile of the board
     * @throws RemoteException related to RMI
     */
    ItemTile getBoardItemTile(int x, int y, String gameName) throws RemoteException;

    /**
     * Returns the number of players of the game
     * @param gameName the game name
     * @return the number of players of the game
     * @throws RemoteException related to RMI
     */
    int getNumberOfPlayers(String gameName) throws RemoteException;

    /**
     * Returns the nickname of a player in the list players in Game
     * @param i the position in the list
     * @param gameName the game name
     * @return the nickname of a player in the list players in Game
     * @throws RemoteException related to RMI
     */
    String getNicknamePlayerNumber(int i, String gameName) throws RemoteException;

    /**
     * Returns the number of common goal cards in the game
     * @param gameName the game name
     * @return the number of common goal cards in the game
     * @throws RemoteException related to RMI
     */
    int getNumberOfCommonGoals(String gameName) throws RemoteException;

    /**
     * Returns the first token available for a common goal card
     * @param i the position of the common goal
     * @param gameName the game name
     * @return the number of common goal cards in the game
     * @throws RemoteException related to RMI
     */
    int getFirstTokenCommonGoal(int i, String gameName) throws RemoteException;

    /**
     * Gets the persistence state of the last player
     * @param gameName the game name
     * @return string with the information
     * @throws RemoteException related to RMI
     */
    String getLastPlayerState(String gameName) throws RemoteException;

    /**
     * The player, after entering the coordinates, tries to take a tile. Returns a message
     *
     * @param xCoordinates the x coordinates
     * @param yCoordinates the y coordinates
     * @param nickname the nickname
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    void takeTiles(List<Integer> xCoordinates, List<Integer> yCoordinates, String nickname, String gameName) throws RemoteException;

    /**
     * Returns the tiles asked, in case taken tiles and ordered tiles
     * @param gameName the game name
     * @return the tiles asked, in case taken tiles and ordered tiles
     * @throws RemoteException related to RMI
     */
    List<ItemTile> getTakenTiles(String gameName) throws RemoteException;

    /**
     * The player, after entering the order for placing the tiles, tries to order the tiles. Returns a message
     *
     * @param items the tiles
     * @param order the order
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    void orderTiles(List<ItemTile> items, List<Integer> order, String gameName) throws RemoteException;

    /**
     * The player, after entering the column chosen, tries to place the tiles given in the bookshelf. Returns a message
     *
     * @param nickname the nickname
     * @param items the tiles
     * @param column the column
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    void placeTiles(String nickname, List<ItemTile> items, int column, String gameName) throws RemoteException;

    /**
     * Returns true if a player has finished a common goal card
     * @param nickname the nickname
     * @param numberCommonGoal the common goal number
     * @param gameName the game name
     * @return true if a player has finished a common goal card
     * @throws RemoteException related to RMI
     */
    boolean playerHasFinishedCommonGoal(String nickname, int numberCommonGoal, String gameName) throws RemoteException;

    /**
     * Returns true if a player has finished the bookshelf
     * @param nickname the nickname
     * @param gameName the game name
     * @return true if a player has finished the bookshelf
     * @throws RemoteException related to RMI
     */
    boolean playerHasFinishedBookshelf(String nickname, String gameName) throws RemoteException;

    /**
     * removes all the parameters of the specific game from their lists
     * @param gameName the game name
     * @throws RemoteException related to RMI
     * @throws InterruptedException related to RMI
     */
    void removeParameters(String gameName) throws RemoteException, InterruptedException;

    /**
     * adds a new client observer to the list
     * @param updateManager the update manager
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    void askUpdateManager(ClientObserver updateManager, String gameName) throws RemoteException;

    /**
     * Removes an update manager from the list
     * @param clientObserver the observer
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    void removeUpdateManager(ClientObserver clientObserver, String gameName) throws RemoteException;

    /**
     * Method that updates the message in all of the observers
     * @param message the message
     * @param i the observer number
     * @param gameName the game name
     * @throws RemoteException related to RMI
     * @return a boolean
     */
    boolean notifyObservers(SocketRMIMessages message, int i, String gameName) throws RemoteException;

    /**
     * method that checks if a player has already printed the information of this turn
     * @param gameName the game name
     * @param nickname the nickname
     * @throws RemoteException related to RMI
     * @return if a player has already print the information of this turn
     */
    boolean playerHasAlreadyPrintedInformationTurn(String gameName, String nickname) throws RemoteException;

    /**
     * Method that gets the value of the attribute PlayerHasPrintedInformationTurn from a player
     * @param gameName the game name
     * @param nickname the nickname
     * @return the value of the attribute, or false if the player is not in the game
     * @throws RemoteException related to RMI
     */
    boolean getPlayerHasPrintedInformationTurn(String gameName, String nickname) throws RemoteException;

    /**
     * getter for the current player's nickname
     * @param gameName the game name
     * @return the nickname of the current player
     * @throws RemoteException related to RMI
     */
    String getCurrentPlayer(String gameName) throws RemoteException;

}



