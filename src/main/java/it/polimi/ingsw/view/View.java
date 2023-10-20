package it.polimi.ingsw.view;

import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;
import it.polimi.ingsw.model.ItemTile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

/**
 * The view interface for implementing TUI and GUI
 */
public interface View {

    /**
     * Method used at the beginning of the game
     */
    void startNewGame();

    /**
     * Method used in order to show the current games' lobbies
     * @param gameName name of the game
     * @param numberOfPlayersJoined number of players that already joined
     * @param numberOfPlayersGame size of the lobby
     */
    void printAvailableGames(List<String> gameName, List<Integer> numberOfPlayersJoined, List<Integer> numberOfPlayersGame);

    /**
     * Method used in order to insert the game name
     * @return the game name
     */
    String askGameName();

    /**
     * Method for asking the nickname
     * @return the asked nickname
     */
    String askNickname();

    /**
     * Method for asking the number of players that will join the match
     * @return the asked number of players
     */
    int askNumberOfPlayers();

    /**
     * Printing errore message
     * @param message the error message
     */
    void printMessage(SocketRMIMessages message);

    /**
     * Method used when a player is waiting that all players join the lobby
     */
    void printAllPlayersJoining();

    /**
     * Method used when all players have joined the lobby
     */
    void printAllPlayersHaveJoined();

    /**
     * Method used to print the personal goal card
     * @param personalGoalCard the personal goal card
     * @param numberOfRowsBookshelf number of rows of the bookshelf
     * @param numberOfColumnsBookshelf number of columns of the bookshelf
     * @throws IOException related to an error while reading or writing on a file
     * @throws URISyntaxException related to an error while reading or writing on a file
     */
    void printPersonalGoalCard(String[][] personalGoalCard, int numberOfRowsBookshelf, int numberOfColumnsBookshelf) throws URISyntaxException, IOException;

    /**
     * Method used to print the way turns are managed
     * @param turnsNickname the ordered list of nicknames
     */
    void printTurns(List<String> turnsNickname);

    /**
     * Method used when a player is waiting for his turn
     */
    void printWaitingForYourTurn();

    /**
     * Method used when it's player's turn
     */
    void isPlayerTurn();

    /**
     * Method used to ask the number of tiles that the player wants to get
     * @return the asked number
     */
    int askNumberOfTilesWanted();

    /**
     * Method used to ask the coordinates in which the player wants to insert the tiles
     * @param numberOfTilesWanted number of tiles wanted
     * @param numberOfRowsBoard number of rows of the board
     * @param numberOfColumnsBoard number of columns of the board
     * @return the coordinates of the asked tiles
     */
    List<Integer> askCoordinates(int numberOfTilesWanted, int numberOfRowsBoard, int numberOfColumnsBoard);

    /**
     * Method used to print the tiles just taken
     * @param takenTiles the taken tiles
     */
    void printTakenTiles(List<ItemTile> takenTiles);

    /**
     * Method used to ask in which order the tiles have to be placed
     * @param numberOfTilesWanted number of tiles wanted
     * @return the desired order
     */
    List<Integer> askOrderTile(int numberOfTilesWanted);

    /**
     * Method used to ask in which column the tiles should be placed
     * @param numberOfColumns number of columns in the bookshelf
     * @return the asked column
     */
    int askColumnPlaceTile(int numberOfColumns);

    /**
     * Method to inform the achievement of a common goal
     * @param number common goal number
     */
    void completedCommonGoalCard(int number);

    /**
     * Method used to inform that the bookshelf is full and the game is ending
     */
    void playerHasFullBookshelf();

    /**
     * Prints a message, used for printPersonalBookshelf
     */
    void printThisIsYourBookShelf();

    /**
     * Method that prints a single bookshelf, used for both printPersonalBookshelf and printAllBookshelf
     * @param personalBookshelf the bookshelf
     * @param numberOfRowsBookshelf number of rows of the bookshelf
     * @param numberOfColumnsBookshelf number of columns of the bookshelf
     */
    void printPersonalBookshelf(String[][] personalBookshelf, int numberOfRowsBookshelf, int numberOfColumnsBookshelf);

    /**
     * Method that prints the scores for the game for each player
     * @param scores the scores of the players
     */
    void printScores(HashMap<String, Integer> scores);

    /**
     * Method that prints the winner of the game
     * @param winner the winner
     */
    void printWinner(String winner);

    /**
     * Method used to inform that the game has ended
     */
    void printTheGameHasEnded();

    /**
     * Method that prints all the information of the player view.It prints the board,the player bookshelf,other
     * players' bookshelf,the personal goal and the commons goals
     * @param CurrentP nickname of the current player
     * @param board the board
     * @param numberOfRowsBoard number of rows of the board
     * @param numberOfColumnsBoard number of columns of the board
     * @param numberOfRowsBookshelf number of rows of the bookshelf
     * @param numberOfColumnsBookshelf number of columns of the bookshelf
     * @param AllPlayersBookshelf bookshelf of all players
     * @param commonGoalDescription description of every common goal of the game
     * @param commonGoalPoints point for each common goal
     * @param nicknames list of all nicknames
     * @param personalGoalCard personal goal card of the player
     * @param currentPlayerBookshelf bookshelf of the player
     * @param newBook bookshelf of all players, on turn excluded
     * @throws IOException related to an error while reading or writing on a file
     * @throws URISyntaxException related to an error while reading or writing on a file
     */
    void printAllInformation(String CurrentP, ItemTile[][] board, int numberOfRowsBoard, int numberOfColumnsBoard, int numberOfRowsBookshelf, int numberOfColumnsBookshelf, List<ItemTile[][]> AllPlayersBookshelf, List<String> commonGoalDescription, List<Integer> commonGoalPoints, List<String> nicknames, String[][] personalGoalCard, ItemTile[][] currentPlayerBookshelf, List<ItemTile[][]> newBook) throws URISyntaxException, IOException;

    /**
     * Method for printing that the server or a player has disconnected
     * @param server 0 for RMI client and server crash, 1 for RMI client and player crash, 2 for socket client
     */
    void printDisconnection(int server);

}