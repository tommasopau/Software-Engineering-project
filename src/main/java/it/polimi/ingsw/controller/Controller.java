package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * The controller for managing the game
 */
public class Controller
{
    private MyShelfieGame game;
    private int numberOfPlayers;
    private List<Integer> orderedPlayers;
    private int playerOnTurn;
    private boolean gameWasAlreadyCreated = false;
    private String lastPlayerPersistenceState = "START";
    private int numberOfPlayersConnected = 0;
    private List<Player> playersConnected = new ArrayList<>();

    /**
     * Constructor method
     */
    public Controller()
    {
        orderedPlayers = new ArrayList<>();
    }

    /**
     * Adding a new player to a game. The first player will indicate how many people will join the match.
     * @param nickname nickname of the player
     * @param numberOfPlayers the first player will insert the number of players for the game
     * @throws NicknameAlreadyTakenException This method checks if a player tries to join a gamer with a nickname that has been already taken
     * @throws NicknameTooLongException A player can have a nickname with 16 or fewer characters
     * @throws FullGameException This method checks if a player is trying to join a game that already started
     * @throws NicknameNotValidCharactersException A nickname must contain at least a character
     * @throws IOException related to an error while reading or writing on file
     * @throws URISyntaxException related to an error while reading or writing on file
     */
    public void addNewPlayer(String nickname, int numberOfPlayers) throws NicknameAlreadyTakenException, NicknameTooLongException, FullGameException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        boolean nicknameAlreadyTaken = false;

        if (game == null) //Checking if it is the first player
        {
            if (numberOfPlayers < 2 || numberOfPlayers > 4) //Checking if it's a correct number of players
            {
                throw new IllegalArgumentException();
            }
            this.numberOfPlayers = numberOfPlayers;
            game = new MyShelfieGame(numberOfPlayers);
        }

        if (game.getPlayers().size() != 0) { //Checking if a nickname is already taken
            for (Player player : game.getPlayers()) {
                if (player.getNickname().equals(nickname)) {
                    nicknameAlreadyTaken = true;
                }
            }
        }

        if (nicknameAlreadyTaken == true) {
            throw new NicknameAlreadyTakenException();
        }
        if (nickname.length() > 16) //Checking the length of the nickname
        {
            if (game.getPlayers().size() == 0) {
                game = null;
            }
            throw new NicknameTooLongException();
        }
        if (game.getPlayers().size() == this.numberOfPlayers) //Checking if the game is full
        {
            throw new FullGameException();
        }
        if (!nickname.matches(".*[a-zA-Z0-9]+.*")) {
            if (game.getPlayers().size() == 0) {
                game = null;
            }
            throw new NicknameNotValidCharactersException();
        }

        Player p = new Player(nickname);
        game.addPlayer(p); //Adding the new player
        playersConnected.add(p);

        numberOfPlayersConnected++;

        if (game.getPlayers().size() == this.numberOfPlayers) //Starting the game if the game is full
        {
            boolean gameHasStarted = true;
            game.start();

            //Creating file for saving the name of the game
            String fileNameGameNames = System.getProperty("user.dir") + "/Persistence/GameNames.txt";

            File fileGameNames = new File(fileNameGameNames);
            if (fileGameNames.exists() == false) {
                fileGameNames.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(fileNameGameNames, true);
            fileWriter.write(game.getGameName() + "\n");
            fileWriter.close();

            //Creating file for saving the turns
            String fileNameTurns = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "Game.txt";

            File file = new File(fileNameTurns);
            if (file.exists() == false) {
                file.createNewFile();
            }

            //Choosing, randomly, the turns of the game
            Random rnd = new Random();
            List<Integer> playersCopy = new ArrayList<>();
            for (int i = 0; i < this.numberOfPlayers; i++) {
                playersCopy.add(i);
            }

            for (int i = 0; i < this.numberOfPlayers; i++) {
                int chosenPlayer = rnd.nextInt(playersCopy.size());
                int player = playersCopy.get(chosenPlayer);
                playersCopy.remove(chosenPlayer);
                orderedPlayers.add(player);

                //Writing turns on file
                fileWriter = new FileWriter(fileNameTurns, true);
                fileWriter.write(player + "\n");
                fileWriter.close();


                game.getPlayers().get(player).setPlacement(i);
                if (i == 0) {
                    game.getPlayers().get(player).setFirstPlayerSeat(true);
                }
            }

            playerOnTurn = 0;
            writePlayerOnTurnFile("START");
        }
    }

    /**
     * This method add a player to a game which was interrupted by a crash of the server
     * @param nickname the nickname of the player
     * @throws NicknameAlreadyTakenException if the player with a given nickname already entered the lobby
     * @throws FullGameException if the game already started
     * @throws NicknameNotFoundPersistenceException if the nickname is not one of the nicknames of the match before the crash
     */
    public void addNewPlayerPersistence(String nickname) throws NicknameAlreadyTakenException, FullGameException, NicknameNotFoundPersistenceException {
        boolean nicknameAlreadyTaken = false;
        boolean nicknameFound = false;

        //Checking if all players already joined
        if(numberOfPlayersConnected == numberOfPlayers)
        {
            throw new FullGameException();
        }

        //Checking if the nickname has been already taken
        for(int i = 0; i < numberOfPlayersConnected; i++)
        {
            if(playersConnected.get(i).getNickname().equals(nickname))
            {
                nicknameAlreadyTaken = true;
            }
        }
        if(nicknameAlreadyTaken == true)
        {
            throw new NicknameAlreadyTakenException();
        }

        Player player = null;
        //Checking if the nickname is corresponding to the nickname of one of the players in the past
        for(int i = 0; i < numberOfPlayers; i++)
        {
            if(game.getPlayers().get(i).getNickname().equals(nickname))
            {
                nicknameFound = true;
                player = game.getPlayers().get(i);
            }
        }
        if(nicknameFound == false)
        {
            throw new NicknameNotFoundPersistenceException();
        }

        game.getPlayers().get(numberOfPlayersConnected).setPlacement(numberOfPlayersConnected);
        playersConnected.add(player);
        numberOfPlayersConnected++;
    }

    /**
     * Used to take one,two or three Tiles from the board
     * @param xCoordinates The abscissas of the Tiles requested
     * @param yCoordinates The ordinates of the Tiles requested
     * @return a list made of the Tiles requested
     * @throws NumberOfTilesNotValidException if the number isn't valid(different from 1,2,3)
     * @throws NotTakeableTileException if the Tile can't be taken
     * @throws NotValidConfigurationException if the requested Tiles aren't in a row or in a column
     * @throws DuplicateTakeTilesException if the player tries to take the same Tile for more than one time
     * @throws NotEnoughTakeableTilesException if the player tries to take more Tiles than the maximum number of tiles that can be taken
     * @throws IOException related to an error while reading or writing on file
     */
    public List<ItemTile> takeTiles(List<Integer> xCoordinates, List<Integer> yCoordinates) throws NumberOfTilesNotValidException, NotTakeableTileException, NotValidConfigurationException, DuplicateTakeTilesException, NotEnoughTakeableTilesException, IOException {
        Board myBoard = game.getGameBoard();
        boolean flagx = true;
        boolean flagy = true;
        boolean flagConsecutivex = true;
        boolean flagConsecutivey = true;
        boolean[][] takeable = myBoard.takeableTiles();
        List<ItemTile> itemList = new ArrayList<>();

        //Checking if more thant the 3 tiles are asked or the size of xCoordinates is different from the size of yCoordinates
        if (xCoordinates.size() == 0 || xCoordinates.size() > 3 || yCoordinates.size() == 0 || yCoordinates.size() > 3 || xCoordinates.size() != yCoordinates.size())
            throw new NumberOfTilesNotValidException();

        //Checking if the player tries to take more tiles than the maximum number of tiles that can be taken
        if (xCoordinates.size() > getGame().getGameBoard().numberOfTakeableTiles()) {
            throw new NotEnoughTakeableTilesException();
        }

        //Checking if the player tries to take the same Tile for more than one time
        for (int i = 0; i < xCoordinates.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (xCoordinates.get(i) == xCoordinates.get(j) && yCoordinates.get(i) == yCoordinates.get(j)) {
                    throw new DuplicateTakeTilesException();
                }
            }
        }

        //Checking if the player tries to take a tile that cannot be taken
        for (int i = 0; i < xCoordinates.size(); i++) {
            if (takeable[yCoordinates.get(i)][xCoordinates.get(i)]) {
                itemList.add(myBoard.getBoard()[yCoordinates.get(i)][xCoordinates.get(i)]);
            } else throw new NotTakeableTileException();
        }

        //Checking if the asked tiles are in a correct configuration
        if (xCoordinates.size() == 2 || xCoordinates.size() == 3) {
            for (int j = 0; j < xCoordinates.size() - 1; j++) {
                if (!xCoordinates.get(j).equals(xCoordinates.get(j + 1))) {
                    flagx = false;
                }

            }
            for (int j = 0; j < yCoordinates.size() - 1; j++) {
                if (!yCoordinates.get(j).equals(yCoordinates.get(j + 1))) {
                    flagy = false;
                }
            }
            if (flagx == false) {
                for (int j = 0; j < xCoordinates.size(); j++) {
                    if (xCoordinates.contains(xCoordinates.get(j) - 1) == false && xCoordinates.contains(xCoordinates.get(j) + 1) == false) {
                        flagConsecutivex = false;
                    }
                }
            }
            if (flagy == false) {
                for (int j = 0; j < yCoordinates.size(); j++) {
                    if (yCoordinates.contains(yCoordinates.get(j) - 1) == false && yCoordinates.contains(yCoordinates.get(j) + 1) == false) {
                        flagConsecutivey = false;
                    }
                }
            }

            if ((flagx == false && flagy == false)) throw new NotValidConfigurationException();
            if (flagx == false && flagConsecutivex == false) throw new NotValidConfigurationException();
            if (flagy == false && flagConsecutivey == false) throw new NotValidConfigurationException();
        }

        //Replacing the taken tiles with empty tiles
        for (int i = 0; i < xCoordinates.size(); i++) {
            myBoard.getBoard()[yCoordinates.get(i)][xCoordinates.get(i)] = new ItemTile(ItemTileType.EMPTY, 0);
        }

        writePlayerOnTurnFile("TAKEN");

        //Writing on file the new Board
        game.writeBoardOnFile();

        if (myBoard.refillNeeded()) {
            myBoard.refill(game.getGameName());
        }

        //Creating file for saving that a player has placed a tile
        String fileNameTilesTaken = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "TilesTaken.txt";

        File file = new File(fileNameTilesTaken);
        if (file.exists() == false) {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(fileNameTilesTaken);
        for (ItemTile itemTile : itemList) {
            fileWriter.write(itemTile.getItemTileType().toString() + "\n");
            fileWriter.write(itemTile.getType() + "\n");
        }
        fileWriter.close();

        return itemList;
    }

    /**
     * Method to choose in which order the tiles will be added to the bookshelf
     * @param items tiles to be ordered
     * @param order order desired
     * @return the ordered tiles
     * @throws NotValidNumberOrderException if order contains a negative number, a not valid number, or items and order have different sizes
     * @throws DuplicateNumberOrderException if order contains a duplicate value
     * @throws IOException related to an error while reading or writing on file
     */
    public List<ItemTile> chooseOrder(List<ItemTile> items, List<Integer> order) throws NotValidNumberOrderException, DuplicateNumberOrderException, IOException {
        //Checking if order is a valid number and items and order have the same size
        for (int i = 0; i < order.size(); i++) {
            if (order.get(i) < 0 || order.get(i) >= order.size() || items.size() != order.size()) {
                throw new NotValidNumberOrderException();
            }
        }

        //Checking if order contains a duplicate value
        for (int i = 0; i < order.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (order.get(i) == order.get(j)) {
                    throw new DuplicateNumberOrderException();
                }
            }
        }

        //Ordering the tiles
        List<ItemTile> finalList = new ArrayList<>();
        for (Integer i : order) {
            finalList.add(items.get(i));
        }

        //Creating file for saving that a player has placed a tile
        String fileNameTilesTaken = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "TilesTaken.txt";

        File file = new File(fileNameTilesTaken);
        if (file.exists() == false) {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(fileNameTilesTaken);
        for (ItemTile itemTile : finalList) {
            fileWriter.write(itemTile.getItemTileType().toString() + "\n");
            fileWriter.write(itemTile.getType() + "\n");
        }
        fileWriter.close();

        writePlayerOnTurnFile("ORDERED");

        return finalList;
    }

    /**
     * Method for placing the tiles in a column
     * @param player that places the tiles in his bookshelf
     * @param items the tiles that the player wants to place in a defined order
     * @param column where the player wants to put his tiles
     * @throws NotEnoughSpaceInColumnException if in the column there isn't enough space for the group of tiles
     * @throws RowAndColumnNotFoundException if a user try to add a tile in a full column
     * @throws IOException related to an error while reading or writing on file
     */
    public void placeTiles(Player player, List<ItemTile> items, int column) throws NotEnoughSpaceInColumnException, RowAndColumnNotFoundException, IOException {
        List<CommonGoalCard> commonGoalCards = game.getCommonGoals();
        List<Integer> commonScores = player.getCommonScores();
        Bookshelf bookshelf = player.getBookshelf();
        ItemTile[][] b = bookshelf.getBookshelf();
        int r = 6; //Number of rows of the bookshelf
        int size = items.size();
        int count = 0;
        for (int i = r - 1; i >= 0 && count < size; i--) {
            if (b[i][column].getItemTileType() == ItemTileType.EMPTY) {
                count++;
            }
        }

        //Creating file for saving that a player has placed a tile
        String fileNameBookshelf = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "Bookshelf.txt";

        File file = new File(fileNameBookshelf);
        if (file.exists() == false) {
            file.createNewFile();
        }

        //If the number of the free positions is the same of the number of the tiles in the list, place the item
        if (count == size) {
            FileWriter fileWriter = new FileWriter(fileNameBookshelf, true);
            for (ItemTile item : items) {
                bookshelf.addTile(item, column);
                fileWriter.write(getPlayerOnTurn().getNickname() + "\n" + item.getItemTileType() + "\n" + item.getType() + "\n" + column + "\n");
            }
            fileWriter.close();

        } else {
            throw new NotEnoughSpaceInColumnException();
        }
        //Checking the common goal
        int i = 0;

        String fileNameCommonGoal = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "CommonGoalCard.txt";

        File fileCommon = new File(fileNameCommonGoal);
        if (fileCommon.exists() == false) {
            fileCommon.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(fileNameCommonGoal, true);

        for (CommonGoalCard card : commonGoalCards) {
            if (commonScores.get(i) == 0 && card.commonGoalAchieved(bookshelf)) {
                commonScores.set(i, card.getOnTop());
                fileWriter.write(getPlayerOnTurn().getNickname() + "\n" + i + "\n");
            }
            i++;
        }
        fileWriter.close();
        //Checking if the player has finished (full bookshelf)
        if (player.getBookshelf().isFull()) {
            playerHasFinished();
        }
        //Checking if the game ended
        if (getNextPlayer() == null) {
            endGame();
        }
        nextTurn();

        writePlayerOnTurnFile("START");
    }

    /**
     * Method for changing the actual player
     * If the game is finished, there will be not a next player
     */
    public void nextTurn()
    {
        //Case next turn is not first player
        if(playerOnTurn + 1 < game.getPlayers().size())
        {
            playerOnTurn++;
        }
        //Case next turn is first player
        else if(playerOnTurn + 1 == game.getPlayers().size() && game.isFinalTokenTaken() == false)
        {
            playerOnTurn = 0;
        }
        //Case game finished
        else if(playerOnTurn + 1 == game.getPlayers().size() && game.isFinalTokenTaken() == true)
        {
            playerOnTurn = -1;
        }
    }

    /**
     * Returns the player in turn if game has finished, null otherwise
     * @return the player in turn
     */
    public Player getPlayerOnTurn()
    {
        //Case game has not finished
        if(playerOnTurn >= 0)
        {
            return game.getPlayers().get(orderedPlayers.get(playerOnTurn));
        }
        //Case game has finished
        else
        {
            return null;
        }
    }

    /**
     * Returns the next player in the turn
     * Returns null if the game is finished
     * @return the next player
     */
    public Player getNextPlayer()
    {
        //Case game finished and current player is already null
        if(getPlayerOnTurn() == null)
        {
            return null;
        }
        //Case next turn is not first player
        if(playerOnTurn + 1 < game.getPlayers().size())
        {
            return game.getPlayers().get(orderedPlayers.get(playerOnTurn + 1));
        }
        //Case next turn is first player
        else if(playerOnTurn + 1 == game.getPlayers().size() && game.isFinalTokenTaken() == false)
        {
            return game.getPlayers().get(orderedPlayers.get(0));
        }
        //Case game finished
        else
        {
            return null;
        }
    }

    /**
     * Method for changing finalTokenTaken and assign that token to the current player
     * @throws IOException related to an error while reading or writing on file
     */
    public void playerHasFinished() throws IOException {
        if (game.isFinalTokenTaken() == false) {
            game.setFinalTokenTaken(true);
            getPlayerOnTurn().setFinalToken(true);

            //Creating file for saving that a player has finished the game
            String fileNameTurns = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "Game.txt";

            File file = new File(fileNameTurns);
            if (file.exists() == false) {
                file.createNewFile();
            }

            //Saving that first player that has finished the game
            FileWriter fileWriter = new FileWriter(fileNameTurns, true);
            fileWriter.write(getPlayerOnTurn().getNickname() + "\n");
            fileWriter.close();
        }
    }

    /**
     * Every player calculates the final score and a winner is chosen
     */
    public void endGame()
    {
        //Calculate final score for every player
        for(Player player: game.getPlayers())
        {
            player.calculateTotalScore();
        }

        //Choosing the winner
        int highestScore = -1;
        Player winner = game.getPlayers().get(orderedPlayers.get(0));

        for(int i = 0; i < game.getPlayers().size(); i++)
        {
            int playerPoints = game.getPlayers().get(orderedPlayers.get(i)).getFinalScore();
            if (playerPoints >= highestScore)
            {
                highestScore = playerPoints;
                winner = game.getPlayers().get(orderedPlayers.get(i));
            }
        }
        game.setWinner(winner);
    }

    /**
     * This method cancel all the files related to a certain game that were created for persistence
     * @throws IOException related to an error while reading or writing on file
     */
    public void removeFilePersistence() throws IOException {
        //Exit if the game does not exist
        if (game == null) {
            return;
        }

        //Searching for al possible files that have been created
        String fileNameBag = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "Bag.txt";
        String fileNameBoard = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "Board.txt";
        String fileNameBookshelf = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "Bookshelf.txt";
        String fileNameCommon = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "CommonGoalCard.txt";
        String fileNameGame = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "Game.txt";
        String fileNamePersonal = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "PersonalGoalCard.txt";
        String fileNamePlayerOnTurn = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "PlayerOnTurn.txt";
        String fileNameTilesTaken = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "TilesTaken.txt";

        File fileBag = new File(fileNameBag);
        File fileBoard = new File(fileNameBoard);
        File fileBookshelf = new File(fileNameBookshelf);
        File fileCommon = new File(fileNameCommon);
        File fileGame = new File(fileNameGame);
        File filePersonal = new File(fileNamePersonal);
        File filePlayerOnTurn = new File(fileNamePlayerOnTurn);
        File fileTilesTaken = new File(fileNameTilesTaken);

        //Deleting the files if they exist
        if (fileBag.exists()) {
            //Files.delete(Paths.get(fileNameBag));
            fileBag.delete();
            fileBoard.delete();
            fileBookshelf.delete();
            fileCommon.delete();
            fileGame.delete();
            filePersonal.delete();
            filePlayerOnTurn.delete();
            fileTilesTaken.delete();

            //Removing the game name from the file that contains the game names
            String fileNameGameNames = System.getProperty("user.dir") + "/Persistence/GameNames.txt";
            File file = new File(fileNameGameNames);
            if (file.exists() == false) {
                file.createNewFile();
            }
            List<String> gameNames = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                gameNames.add(scanner.nextLine());
            }

            scanner.close();

            gameNames.remove(game.getGameName());

            FileWriter fileWriter = new FileWriter(file);
            for (String gameName : gameNames) {
                fileWriter.write(gameName + "\n");
            }
            fileWriter.close();
        }
    }

    /**
     * Writes the last move of a player before the server crash
     * @param state the last move
     * @throws IOException related to an error while reading or writing on file
     */
    public void writePlayerOnTurnFile(String state) throws IOException {
        //Creating file for saving the player on turn
        String fileNamePlayerOnTurn = System.getProperty("user.dir") + "/Persistence/" + game.getGameName() + "PlayerOnTurn.txt";

        File filePlayerOnTurn = new File(fileNamePlayerOnTurn);
        if (filePlayerOnTurn.exists() == false) {
            filePlayerOnTurn.createNewFile();
        }

        FileWriter fileWriter = null;
        fileWriter = new FileWriter(fileNamePlayerOnTurn);
        fileWriter.write(playerOnTurn + "\n" + state);
        fileWriter.close();
    }

    /**
     * Getter method for parameter game
     * @return game
     */
    public MyShelfieGame getGame() {
        return game;
    }

    /**
     * Getter method for parameter numberOfPlayers
     * @return numberOfPlayers
     */
    public int getNumberOfPlayers()
    {
        return numberOfPlayers;
    }

    /**
     * Getter method for list orderedPlayers
     * @return the list orderedPlayers
     */
    public List<Integer> getOrderedPlayers() {
        return orderedPlayers;
    }

    /**
     * Method for creating a new game give the number of players
     * @param numberOfPlayers number of players
     * @throws IOException related to an error while reading or writing on file
     * @throws URISyntaxException related to an error while reading or writing on file
     */
    public void createGame(int numberOfPlayers) throws URISyntaxException, IOException {
        game = new MyShelfieGame(numberOfPlayers);
    }

    /**
     * Setter method for number of players
     * @param numberOfPlayers number of players
     */
    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    /**
     * Getter method for gameWasAlreadyCreated
     * @return gameWasAlreadyCreated
     */
    public boolean isGameWasAlreadyCreated() {
        return gameWasAlreadyCreated;
    }

    /**
     * Setter method for GameWasAlreadyCreated
     * @param gameWasAlreadyCreated the boolean
     */
    public void setGameWasAlreadyCreated(boolean gameWasAlreadyCreated) {
        this.gameWasAlreadyCreated = gameWasAlreadyCreated;
    }

    /**
     * Getter method for lastPlayerPersistenceState
     * @return lastPlayerPersistenceState
     */
    public String getLastPlayerPersistenceState() {
        return lastPlayerPersistenceState;
    }

    /**
     * Setter method for lastPlayerPersistenceState
     * @param lastPlayerPersistenceState the state
     */
    public void setLastPlayerPersistenceState(String lastPlayerPersistenceState) {
        this.lastPlayerPersistenceState = lastPlayerPersistenceState;
    }

    /**
     * Setter method for playerOnTurn
     * @param playerOnTurn the player on turn
     */
    public void setPlayerOnTurn(int playerOnTurn) {
        this.playerOnTurn = playerOnTurn;
    }

    /**
     * Getter method for numberOfPlayersConnected
     * @return numberOfPlayersConnected
     */
    public int getNumberOfPlayersConnected() {
        return numberOfPlayersConnected;
    }
}
