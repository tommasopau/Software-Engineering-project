package it.polimi.ingsw.distributed.socket;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.distributed.Messages.MessagesType;
import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;
import it.polimi.ingsw.distributed.RMI.Disconnection.DisconnectionHandlerRMI;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ClientObserver;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The Socket Server
 */
public class MultiServer
{
    private int port;
    HashMap<Integer, Controller> controller = new HashMap<>();
    HashMap<Integer, Semaphore> semaphoreNumberOfPlayers = new HashMap<>(); //For management of first player joining
    HashMap<Integer, Semaphore> semaphoreTurns = new HashMap<>(); //For management of turns
    HashMap<Integer, Object> waitAllPlayersLock = new HashMap<>();
    HashMap<String, Integer> gameNamesID = new HashMap<>();
    Lock lockAddNewGame = new ReentrantLock();
    AtomicInteger gameIDGlobal;
    HashMap<Integer, List<DisconnectionHandlerRMI>> disconnectionHandlersRMI = new HashMap<>();
    HashMap<Integer, List<Socket>> disconnectionHandlerSocket = new HashMap<>();
    HashMap<Integer, List<ItemTile>> takenTilesRMI;
    HashMap<Integer, AtomicInteger> allPlayersHavePrintEndGame;
    HashMap<Integer, List<ClientObserver>> updateManagers = new HashMap<>();
    HashMap<Integer, List<Boolean>> playerHasPrintedInformationTurn = new HashMap<>();

    /**
     * Constructor of MultiServer
     * @param port the port
     * @param controller hashMap of controllers
     * @param semaphoreNumberOfPlayers hashMap of semaphore for number of players
     * @param semaphoreTurns hashMap of semaphore of turns
     * @param waitAllPlayersLock hashMap of all players lock
     * @param lockAddNewGame Lock for creation of new game
     * @param gameNamesID ID for the games
     * @param gameIDGlobal ID for new games
     * @param disconnectionHandlersRMI hashMap of disconnection handler RMI
     * @param disconnectionHandlerSocket hashMap of disconnection handler Socket
     * @param takenTilesRMI hashMap of tiles taken
     * @param allPlayersHavePrintEndGame hashMap for all players have printed end game
     * @param updateManagers hashMap for update managers
     * @param playerHasPrintedInformationTurn hashMap for player has printed information turn
     * @throws RemoteException related to socket
     */
    public MultiServer(int port, HashMap<Integer, Controller> controller, HashMap<Integer,Semaphore> semaphoreNumberOfPlayers,
                       HashMap<Integer, Semaphore> semaphoreTurns, HashMap<Integer, Object> waitAllPlayersLock,
                       Lock lockAddNewGame, HashMap<String, Integer> gameNamesID, AtomicInteger gameIDGlobal,
                       HashMap<Integer, List<DisconnectionHandlerRMI>> disconnectionHandlersRMI,
                       HashMap<Integer, List<Socket>> disconnectionHandlerSocket, HashMap<Integer, List<ItemTile>> takenTilesRMI,
                       HashMap<Integer, AtomicInteger> allPlayersHavePrintEndGame, HashMap<Integer, List<ClientObserver>> updateManagers,
                       HashMap<Integer, List<Boolean>> playerHasPrintedInformationTurn) throws RemoteException {
        this.port = port;
        this.controller = controller;
        this.semaphoreNumberOfPlayers = semaphoreNumberOfPlayers;
        this.semaphoreTurns = semaphoreTurns;
        this.waitAllPlayersLock = waitAllPlayersLock;
        this.gameNamesID = gameNamesID;
        this.lockAddNewGame = lockAddNewGame;
        this.gameIDGlobal = gameIDGlobal;
        this.disconnectionHandlersRMI = disconnectionHandlersRMI;
        this.disconnectionHandlerSocket = disconnectionHandlerSocket;
        this.takenTilesRMI = takenTilesRMI;
        this.allPlayersHavePrintEndGame = allPlayersHavePrintEndGame;
        this.updateManagers = updateManagers;
        this.playerHasPrintedInformationTurn = playerHasPrintedInformationTurn;
    }

    /**
     * Method for starting the game
     * @param socket the socket
     */
    public void start(Socket socket){
        int gameID = -1;
        String gameName = null;

        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        )
        {
            sendAvailableGames(out);

            gameName = askGameName(in);

            gameID = gameNamesID.get(gameName);

            SocketRMIMessages gameWasAlreadyCreatedPersistenceMessage = gameWasAlreadyCreatedPersistence(gameID);
            out.println(gameWasAlreadyCreatedPersistenceMessage.getMessagesType());

            disconnectionHandlerSocket.get(gameID).add(socket);

            String nickname;
            if(gameWasAlreadyCreatedPersistenceMessage.getMessagesType() == MessagesType.NEW_GAME_PERSISTENCE) {
                //Reading the nickname of the Player
                nickname = joinNewPlayer(in, out, gameID, gameName);
            }
            else
            {
                //Reading the nickname of the Player
                nickname = addPlayerGamePersistence(in, out, gameID, gameName);
            }
            //Waiting that all players join the game
            waitAllPlayers(out, gameID);
            sendTurns(out, gameID);

            SocketRMIMessages gameFinished;
            while (controller.get(gameID).getPlayerOnTurn() != null)
            {
                gameFinished = new SocketRMIMessages(MessagesType.GAME_HAS_NOT_FINISHED);
                out.println(gameFinished.getMessagesType()); //So the client knows when the game is still going on
                newTurn(in, out, nickname, gameID, gameName);
            }

            gameFinished = new SocketRMIMessages(MessagesType.GAME_FINISHED);
            out.println(gameFinished.getMessagesType()); //So the client knows when the game has finished

            //Sending to the Client the scores
            sendScores(out, gameID);
            //Sending to the Client the winner
            sendWinner(out, gameID);

            synchronized (waitAllPlayersLock.get(gameID)) {
                allPlayersHavePrintEndGame.get(gameID).set(allPlayersHavePrintEndGame.get(gameID).get() + 1);
            }

            //Removing all parameters from HashMap
            synchronized (waitAllPlayersLock.get(gameID))
            {
                waitAllPlayersLock.get(gameID).notifyAll();
                while (allPlayersHavePrintEndGame.get(gameID).get() != controller.get(gameID).getNumberOfPlayers())
                {
                    waitAllPlayersLock.get(gameID).wait();
                }
            }

            //synchronized (lockAddNewGame) {
            lockAddNewGame.lock();
                if (controller.containsKey(gameID)) {
                    controller.get(gameID).removeFilePersistence();
                    removeParameters(gameID, gameName);
                }
                lockAddNewGame.unlock();
            //}

        }catch (UnsupportedEncodingException e) {
            System.err.println("Codification of characters not supported: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error for I/O: " + e.getMessage());
            disconnectAllGamePlayers(gameID, gameName);
        } catch (InterruptedException e) {
            System.err.println("Interrupted exception: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found exception" + e.getMessage());
        }catch (NullPointerException ignored)
        {

        }
    }

    /**
     * Method used to send to the client the list of available games' lobbies
     * @param out the printWriter
     */
    public void sendAvailableGames(PrintWriter out)
    {
        lockAddNewGame.lock();

        int numberOfAvailableGames = 0;
        for(int i = 0; i < controller.size(); i++)
        {
            if(controller.get(i).getGame().getGameName() != null)
            {
                numberOfAvailableGames++;
            }
        }
        out.println(numberOfAvailableGames);

        for(Integer controllerID : controller.keySet())
        {
            if(controller.get(controllerID).getGame().getGameName() != null) {
                out.println(controller.get(controllerID).getGame().getGameName());
                out.println(controller.get(controllerID).getNumberOfPlayersConnected());
                out.println(controller.get(controllerID).getNumberOfPlayers());
            }
        }

        lockAddNewGame.unlock();
    }

    /**
     * Method that gets the game name of the match
     * @param in the bufferReader
     * @return the game name
     */
    public String askGameName(BufferedReader in)
    {
        String gameName = null;
        try {
            gameName = in.readLine();

            lockAddNewGame.lock();

            if (gameNamesID.isEmpty() || gameNamesID.containsKey(gameName) == false) {
                gameNamesID.put(gameName, gameIDGlobal.get());
                Controller controllerGame = new Controller();
                controller.put(gameIDGlobal.get(), controllerGame);
                Semaphore semaphoreNumberOfPlayersGame = new Semaphore(1);
                semaphoreNumberOfPlayers.put(gameIDGlobal.get(), semaphoreNumberOfPlayersGame);
                Semaphore semaphoreTurnsGame = new Semaphore(1);
                semaphoreTurns.put(gameIDGlobal.get(), semaphoreTurnsGame);
                Object waitAllPlayersLockGame = new Object();
                waitAllPlayersLock.put(gameIDGlobal.get(), waitAllPlayersLockGame);
                List<DisconnectionHandlerRMI> disconnectionHandlersGameRMI = new ArrayList<>();
                disconnectionHandlersRMI.put(gameIDGlobal.get(), disconnectionHandlersGameRMI);
                List<Socket> disconnectionHandlerSocketGame = new ArrayList<>();
                disconnectionHandlerSocket.put(gameIDGlobal.get(), disconnectionHandlerSocketGame);
                List<ItemTile> takenTilesGame = new ArrayList<>();
                takenTilesRMI.put(gameIDGlobal.get(), takenTilesGame);
                AtomicInteger atomicInteger = new AtomicInteger();
                atomicInteger.set(0);
                allPlayersHavePrintEndGame.put(gameIDGlobal.get(), atomicInteger);
                List<ClientObserver> updateManagersGame = new ArrayList<>();
                updateManagers.put(gameIDGlobal.get(), updateManagersGame);
                List<Boolean> playerHasPrintedInformationTurnGame = new ArrayList<>();
                playerHasPrintedInformationTurn.put(gameIDGlobal.get(), playerHasPrintedInformationTurnGame);

                gameIDGlobal.set(gameIDGlobal.get() + 1);
            }

            lockAddNewGame.unlock();
        } catch (IOException e) {
            System.err.println("Error for I/O: " + e.getMessage());
        }
        return gameName;
    }

    /**
     * method that checks if the game was already created regarding the persistence aspect
     * @param gameID the game ID
     * @return a message with the answer
     */
    public SocketRMIMessages gameWasAlreadyCreatedPersistence(int gameID)
    {
        if(controller.get(gameID).isGameWasAlreadyCreated() == true)
        {
            SocketRMIMessages socketRMIMessages = new SocketRMIMessages(MessagesType.GAME_ALREADY_CREATED_PERSISTENCE);
            return socketRMIMessages;
        }
        else
        {
            SocketRMIMessages socketRMIMessages = new SocketRMIMessages(MessagesType.NEW_GAME_PERSISTENCE);
            return socketRMIMessages;
        }
    }

    /**
     * Method for adding a new player (Client asks to be added to the game. He inserts his nickname and the numberOfPlayer that will play the game)
     * @param in is the BufferedReader used to communicate with the client (reading string)
     * @param out is the PrintWriter used to sending string to the client
     * @param gameID the game ID
     * @param gameName the game name
     * @return the nickname of the player
     * @throws InterruptedException related to socket
     */
    public String joinNewPlayer(BufferedReader in, PrintWriter out, int gameID, String gameName) throws InterruptedException {
        String nickname = null;

        synchronized (waitAllPlayersLock.get(gameID)) {
            SocketRMIMessages correctNickname = new SocketRMIMessages(MessagesType.START);
            SocketRMIMessages correctNumberOfPlayers = new SocketRMIMessages(MessagesType.NUMBER_OF_PLAYERS_NOT_VALID);

            semaphoreNumberOfPlayers.get(gameID).acquire();

            while (correctNickname.getMessagesType() != MessagesType.NICKNAME_CORRECT || correctNumberOfPlayers.getMessagesType() == MessagesType.NUMBER_OF_PLAYERS_NOT_VALID) {
                try {
                    correctNickname = new SocketRMIMessages(MessagesType.START);
                    correctNumberOfPlayers = new SocketRMIMessages(MessagesType.NUMBER_OF_PLAYERS_NOT_VALID);

                    //Read nickname from client
                    nickname = in.readLine();
                    int numberOfPlayers = 2; //Default value
                    int numberOfActualPlayers;

                    //If the game has just started
                    if (controller.get(gameID).getGame() == null) {
                        numberOfActualPlayers = 0;
                        out.println(MessagesType.NUMBER_OF_PLAYERS_NEEDED.toString());
                    } else {
                        numberOfActualPlayers = controller.get(gameID).getGame().getPlayers().size();
                        out.println(MessagesType.NUMBER_OF_PLAYERS_NOT_NEEDED.toString());
                        correctNumberOfPlayers = new SocketRMIMessages(MessagesType.CORRECT_NUMBER_OF_PLAYERS);
                        semaphoreNumberOfPlayers.get(gameID).release();
                    }
                    if (numberOfActualPlayers == 0) {
                        try {
                            numberOfPlayers = Integer.parseInt(in.readLine());
                        } catch (IOException e) {
                            System.err.println("Error for I/O: " + e.getMessage());
                            disconnectAllGamePlayers(gameID, gameName);
                        } catch (NumberFormatException ignored)
                        {

                        }
                        while (correctNumberOfPlayers.getMessagesType() == MessagesType.NUMBER_OF_PLAYERS_NOT_VALID) {
                            if (numberOfPlayers < 2 || numberOfPlayers > 4) {
                                correctNumberOfPlayers = new SocketRMIMessages(MessagesType.NUMBER_OF_PLAYERS_NOT_VALID);
                                out.println(correctNumberOfPlayers.getMessagesType().toString());
                            } else {
                                correctNumberOfPlayers = new SocketRMIMessages(MessagesType.CORRECT_NUMBER_OF_PLAYERS);;
                                out.println(correctNumberOfPlayers.getMessagesType().toString());
                            }
                        }
                    }
                    try {
                        controller.get(gameID).addNewPlayer(nickname, numberOfPlayers);
                        controller.get(gameID).getGame().setGameName(gameName);
                        playerHasPrintedInformationTurn.get(gameID).add(false);
                        correctNickname = new SocketRMIMessages(MessagesType.NICKNAME_CORRECT);
                        waitAllPlayersLock.get(gameID).notifyAll();
                        if (numberOfActualPlayers == 0) {
                            semaphoreNumberOfPlayers.get(gameID).release();
                        }
                    } catch (NicknameAlreadyTakenException e) {
                        correctNickname = new SocketRMIMessages(MessagesType.NICKNAME_TAKEN);
                    } catch (FullGameException e) {
                        correctNickname = new SocketRMIMessages(MessagesType.NICKNAME_FULL_GAME);
                    } catch (NicknameTooLongException e) {
                        correctNickname = new SocketRMIMessages(MessagesType.NICKNAME_TOO_LONG);
                    } catch (NicknameNotValidCharactersException e) {
                        correctNickname = new SocketRMIMessages(MessagesType.NICKNAME_NOT_VALID_CHARACTERS);
                    } catch (URISyntaxException e) {
                        System.err.println("Error while writing on file");
                    }
                    out.println(correctNickname.getMessagesType().toString());
                } catch (UnsupportedEncodingException e) {
                    System.err.println("Codification of characters not supported: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error for I/O: " + e.getMessage());
                    disconnectAllGamePlayers(gameID, gameName);
                    return "";
                }catch (NullPointerException ignored)
                {}
            }
        }
        return nickname;
    }

    /**
     * Method that adds a new player to the persistence list
     * @param in the bufferReader
     * @param out the PrintWriter
     * @param gameID the game ID
     * @param gameName the game name
     * @return a positive answer or an error message
     */
    public String addPlayerGamePersistence(BufferedReader in, PrintWriter out, int gameID, String gameName) {
        String nickname = null;

        synchronized (waitAllPlayersLock.get(gameID)) {
            SocketRMIMessages correctNickname = new SocketRMIMessages(MessagesType.START);
            while (correctNickname.getMessagesType() != MessagesType.NICKNAME_CORRECT) {
                try {
                    nickname = in.readLine();
                    try {
                        controller.get(gameID).addNewPlayerPersistence(nickname);
                        playerHasPrintedInformationTurn.get(gameID).add(false);
                        waitAllPlayersLock.get(gameID).notifyAll();
                        correctNickname = new SocketRMIMessages(MessagesType.NICKNAME_CORRECT);
                    } catch (NicknameAlreadyTakenException e) {
                        correctNickname = new SocketRMIMessages(MessagesType.NICKNAME_TAKEN);
                    } catch (NicknameNotFoundPersistenceException e) {
                        correctNickname = new SocketRMIMessages(MessagesType.NICKNAME_NOT_FOUND_PERSISTENCE);
                    } catch (FullGameException e) {
                        correctNickname = new SocketRMIMessages(MessagesType.NICKNAME_FULL_GAME);
                    }
                } catch (IOException e) {
                    System.err.println("Error for I/O: " + e.getMessage());
                    disconnectAllGamePlayers(gameID, gameName);
                } catch (NullPointerException ignored) {
                }
                out.println(correctNickname.getMessagesType().toString());
            }
        }
        return nickname;
    }

    /**
     * Method for waiting that all players join the game
     * @param out is the PrintWriter used to sending string to the client
     * @param gameID the game ID
     * @throws InterruptedException related to socket
     */
    public void waitAllPlayers(PrintWriter out, int gameID) throws InterruptedException {
        synchronized (waitAllPlayersLock.get(gameID)) {
            while (controller.get(gameID).getNumberOfPlayersConnected() != controller.get(gameID).getNumberOfPlayers()) {
                waitAllPlayersLock.get(gameID).wait();
            }
            SocketRMIMessages startingGame = new SocketRMIMessages(MessagesType.GAME_START);
            out.println(startingGame.getMessagesType().toString());
        }
    }

    /**
     * Method for sending to the client the common goal cards
     * @param out is the PrintWriter used to send objects to the client
     * @param gameID the game ID
     */
    public void sendCommonGoalObjective(PrintWriter out, int gameID) {
        List<CommonGoalCard> cards = new ArrayList<>(controller.get(gameID).getGame().getCommonGoals());
        out.println(cards.size());
        for (CommonGoalCard card : cards) {
            out.println(card.getDescription());
        }
    }

    /**
     * Method used in order to send to the client the Personal Goal Card
     * @param out the printWriter
     * @param nickname the nickname
     * @param gameID the gameID
     */
    public void sendPersonalGoalObjective(PrintWriter out, String nickname, int gameID) {
        List<Player> playerList = controller.get(gameID).getGame().getPlayers();
        Player player = null;
        for (Player p : playerList) {
            if (p.getNickname().equals(nickname)) {
                player = p;
                break;
            }
        }
        int row = player.getPersonalGoalCard().getPersonalGoalCard().length;
        int columns = player.getPersonalGoalCard().getPersonalGoalCard()[0].length;
        out.println(row);
        out.println(columns);

        PersonalGoalCard card = player.getPersonalGoalCard();
        Gson gson = new Gson();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                ItemTile tile = card.getPersonalGoalCard()[i][j];
                String json = gson.toJson(tile);
                out.println(json);
            }
        }
    }

    /**
     * Method used in order to send the game board to the client
     * @param out the printWriter
     * @param gameID the game ID
     */
    public void sendBoard(PrintWriter out, int gameID) {
        Board board = controller.get(gameID).getGame().getGameBoard();
        int row = board.getBoard().length;
        int columns = board.getBoard()[0].length;
        out.println(row);
        out.println(columns);

        Gson gson = new Gson();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ItemTile tile = board.getBoard()[i][j];
                String json = gson.toJson(tile);
                out.println(json);
            }
        }
    }

    /**
     * Method for sending to the client the players' turn (the sequence in which the players will play)
     * @param out is the PrintWriter used to send strings to the client
     * @param gameID the game ID
     * @throws ClassNotFoundException related to socket
     */
    public void sendTurns(PrintWriter out, int gameID) throws ClassNotFoundException {
        int numberOfPlayers = controller.get(gameID).getNumberOfPlayers();
        out.println(numberOfPlayers);
        for(Integer orderedPlayer : controller.get(gameID).getOrderedPlayers())
        {
            out.println(controller.get(gameID).getGame().getPlayers().get(orderedPlayer).getNickname());
        }
    }

    /**
     * Method called for each turn. Checks for all the possible situations of the player and takes all the
     * necessary steps
     * @param in the bufferReader
     * @param out the printWriter
     * @param nickname the nickname
     * @param gameID the game ID
     * @param gameName the game name
     */
    public void newTurn(BufferedReader in, PrintWriter out, String nickname, int gameID, String gameName)
    {
        try {
            if(getPlayerHasPrintedInformationTurn(gameID, nickname) == false)
            {
                SocketRMIMessages playerNotOnTurn;
                playerNotOnTurn = new SocketRMIMessages(MessagesType.NOT_PLAYER_ON_TURN);
                out.println(playerNotOnTurn.getMessagesType().toString());
                playerHasAlreadyPrintedInformationTurn(gameID, nickname, out);
                return;
            }
            semaphoreTurns.get(gameID).acquire();
            if(controller.get(gameID).getPlayerOnTurn() == null) //Case game has ended
            {
                SocketRMIMessages gameEnded;
                gameEnded = new SocketRMIMessages(MessagesType.GAME_FINISHED);
                out.println(gameEnded.getMessagesType().toString());
                semaphoreTurns.get(gameID).release();
                return;
            }
            else if(getPlayerHasPrintedInformationTurn(gameID, nickname) == false)
            {
                SocketRMIMessages playerNotOnTurn;
                playerNotOnTurn = new SocketRMIMessages(MessagesType.NOT_PLAYER_ON_TURN);
                out.println(playerNotOnTurn.getMessagesType().toString());
                semaphoreTurns.get(gameID).release();
                playerHasAlreadyPrintedInformationTurn(gameID, nickname, out);
                return;
            }
            else if (!controller.get(gameID).getPlayerOnTurn().getNickname().equals(nickname)) //If it's not the player on turn
            {
                SocketRMIMessages playerNotOnTurn;
                playerNotOnTurn = new SocketRMIMessages(MessagesType.NOT_PLAYER_ON_TURN);
                out.println(playerNotOnTurn.getMessagesType().toString());
                semaphoreTurns.get(gameID).release();
                playerHasAlreadyPrintedInformationTurn(gameID, nickname, out);
                return;
            }
            else {
                //If it's the player on Turn
                SocketRMIMessages playerOnTurn;
                playerOnTurn = new SocketRMIMessages(MessagesType.PLAYER_ON_TURN);
                out.println(playerOnTurn.getMessagesType().toString());
                extractOrderPlace(in, out, nickname, gameID, gameName);
                semaphoreTurns.get(gameID).release();
            }
        }catch (InterruptedException e) {
            System.err.println("Interrupted exception: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e.getMessage());
        }
        catch (NullPointerException ignored)
        {}
    }

    /**
     * Method used to extract the tiles from the board, choose the order and insert them into the player's bookshelf
     * Checks if all the conditions are met for all the choices to be possible in the game
     * @param in the bufferReader
     * @param out the printWriter
     * @param nickname the nickname
     * @param gameID the game ID
     * @param gameName the game name
     * @throws ClassNotFoundException related to socket
     */
    public void extractOrderPlace(BufferedReader in, PrintWriter out, String nickname, int gameID, String gameName) throws ClassNotFoundException {
        int numberOfTilesWanted = -1;
        boolean correctTake = false;
        List<ItemTile> takenTiles = new ArrayList<>();

        String lastPlayerPersistence = getLastPlayerState(gameID);
        out.println(lastPlayerPersistence);

        if(lastPlayerPersistence.equals("START")) {

            do {
                //Checking if there are columns with a number of spaces >= of the number of tiles wanted from the player
                do {
                    try {
                        numberOfTilesWanted = Integer.parseInt(in.readLine());
                    } catch (IOException e) {
                        System.err.println("Error for I/O: " + e.getMessage());
                        disconnectAllGamePlayers(gameID, gameName);
                    } catch (NumberFormatException ignored)
                    {

                    }
                    if (controller.get(gameID).getPlayerOnTurn().getBookshelf().numberOfPlaceableTiles() < numberOfTilesWanted) {
                        out.println(MessagesType.NOT_ENOUGH_SPACE_TAKE.toString());
                    } else {
                        out.println(MessagesType.CORRECT_TAKE.toString());
                        correctTake = true;
                    }
                } while (!correctTake);

                List<Integer> xCoordinates = new ArrayList<>();
                List<Integer> yCoordinates = new ArrayList<>();

                //Checking if the choice of the player is correct
                correctTake = false;
                for (int i = 0; i < numberOfTilesWanted; i++) {
                    try {
                        xCoordinates.add(Integer.parseInt(in.readLine()));
                    } catch (IOException e) {
                        System.err.println("Error for I/O: " + e.getMessage());
                        disconnectAllGamePlayers(gameID, gameName);
                    } catch (NullPointerException ignored) {
                    }
                }
                for (int i = 0; i < numberOfTilesWanted; i++) {
                    try {
                        yCoordinates.add(Integer.parseInt(in.readLine()));
                    } catch (IOException e) {
                        System.err.println("Error for I/O: " + e.getMessage());
                        disconnectAllGamePlayers(gameID, gameName);
                    } catch (NullPointerException ignored) {
                    } catch (NumberFormatException ignored)
                    {

                    }
                }
                try {
                    takenTiles.addAll(controller.get(gameID).takeTiles(xCoordinates, yCoordinates));
                    out.println(MessagesType.CORRECT_TAKE.toString());
                    correctTake = true;
                    controller.get(gameID).setLastPlayerPersistenceState("START");
                } catch (NumberOfTilesNotValidException e) { //Case entered a position outside the board
                    out.println(MessagesType.NUMBER_OF_TILES_WANTED_NOT_VALID.toString());
                } catch (NotValidConfigurationException e) { //Case asked tiles not in a straight line
                    out.println(MessagesType.NOT_VALID_CONFIGURATION_TAKE.toString());
                } catch (NotTakeableTileException e) { //Case tiles asked are not takeable
                    out.println(MessagesType.NOT_TAKEABLE_TILE.toString());
                } catch (DuplicateTakeTilesException e) { //Case asked a tile for more than two times
                    out.println(MessagesType.DUPLICATE_TAKE_TILE.toString());
                } catch (
                        NotEnoughTakeableTilesException e) { //Case number of tiles wanted is greater than number of takeable tiles
                    out.println(MessagesType.NOT_ENOUGH_TAKEABLE_TILES.toString());
                } catch (IOException e) {
                    System.err.println("Error while writing on file");
                }
                xCoordinates.clear();
                yCoordinates.clear();
            } while (!correctTake);
        }

        //Sending the tiles to the client
        //Case game was already created persistence
        if(takenTiles.size() == 0)
        {
            takenTiles.addAll(takenTilesRMI.get(gameID));
            takenTilesRMI.get(gameID).clear();
        }
        out.println(takenTiles.size());
        Gson gson = new Gson();
        for (ItemTile item : takenTiles) {
            String json = gson.toJson(item);
            out.println(json);
        }

        numberOfTilesWanted = takenTiles.size();
        List<ItemTile> takenTilesCopy = new ArrayList<>();
        if(lastPlayerPersistence.equals("TAKEN") || lastPlayerPersistence.equals("START")) {
            if (takenTiles.size() > 1) {
                if (takenTiles.size() != 2 || takenTiles.get(0).getItemTileType() != takenTiles.get(1).getItemTileType()) {
                    if (numberOfTilesWanted != 3 || !(takenTiles.get(0).getItemTileType() == takenTiles.get(1).getItemTileType() && takenTiles.get(1).getItemTileType() == takenTiles.get(2).getItemTileType())) {
                        //Checking if the order is correct
                        List<Integer> order = new ArrayList<>();
                        boolean correctOrder = false;
                        do {
                            for (int i = 0; i < numberOfTilesWanted; i++) {
                                try {
                                    order.add(Integer.parseInt(in.readLine()));
                                } catch (IOException e) {
                                    System.err.println("Error for I/O: " + e.getMessage());
                                    disconnectAllGamePlayers(gameID, gameName);
                                } catch (NullPointerException ignored) {
                                } catch (NumberFormatException ignored)
                                {}
                            }
                            try {
                                takenTilesCopy.addAll(controller.get(gameID).chooseOrder(takenTiles, order));
                                out.println(MessagesType.CORRECT_ORDER_VALUES.toString());
                                correctOrder = true;
                                takenTiles.clear();
                                takenTiles.addAll(takenTilesCopy);
                                controller.get(gameID).setLastPlayerPersistenceState("START");
                            } catch (NotValidNumberOrderException e) { //Case order contains not valid numbers
                                out.println(MessagesType.NOT_VALID_ORDER_VALUES.toString());
                            } catch (DuplicateNumberOrderException e) { //Case order contains duplicate numbers
                                out.println(MessagesType.DUPLICATE_ORDER_VALUES.toString());
                            } catch (IOException e) {
                                System.err.println("Error while writing on file");
                            }
                        } while (!correctOrder);
                    }
                }
            }
        }

        sendBookshelf(out, gameID,nickname);

        //Placing tiles
        int column = -1;
        boolean correctPlace = false;
        do {
            try {
                column = Integer.parseInt(in.readLine());
            } catch (IOException e) {
                System.err.println("Error for I/O: " + e.getMessage());
                disconnectAllGamePlayers(gameID, gameName);
            }
            catch (NumberFormatException ignored)
            {

            }
            try {
                for(Player player : controller.get(gameID).getGame().getPlayers())
                {
                    if(player.getNickname().equals(nickname))
                    {
                        controller.get(gameID).placeTiles(player, takenTiles, column);
                        out.println(MessagesType.CORRECT_VALUE_PLACE.toString());
                        correctPlace = true;
                        controller.get(gameID).setLastPlayerPersistenceState("START");
                    }
                }
            } catch (NotEnoughSpaceInColumnException e) { //Case column is full or doesn't have enough space
                out.println(MessagesType.NOT_ENOUGH_SPACE_IN_COLUMN.toString());
            } catch (RowAndColumnNotFoundException e) { //Case column is not a correct value
                out.println(MessagesType.NOT_CORRECT_VALUE_PLACE.toString());
            } catch (IOException e) {
                System.err.println("Error while writing on file");
            }
        }while(!correctPlace);

        for(int j = 0; j < playerHasPrintedInformationTurn.get(gameID).size(); j++)
        {
            playerHasPrintedInformationTurn.get(gameID).set(j, false);
        }

        //Possible achievement of a Common Goal
        int numberOfCommonGoals = controller.get(gameID).getGame().getCommonGoals().size();
        for(Player player : controller.get(gameID).getGame().getPlayers()) {
            if(player.getNickname().equals(nickname)) {
                for (int i = 0; i < numberOfCommonGoals; i++) {
                    out.println(player.getCommonScores().get(i));
                }
            }
        }

        //Possible filling of the bookshelf
        for(Player player : controller.get(gameID).getGame().getPlayers()) {
            if(player.getNickname().equals(nickname)) {
                if (player.getBookshelf().isFull()) {
                    out.println(MessagesType.FULL_BOOKSHELF.toString());
                } else {
                    out.println(MessagesType.NOT_FULL_BOOKSHELF.toString());
                }
            }
        }
    }

    /**
     * Getter for the persistence state of the last player
     * @param gameID the game ID
     * @return a string with the information
     */
    public String getLastPlayerState(int gameID)
    {
        String lastPlayerPersistenceState = controller.get(gameID).getLastPlayerPersistenceState();
        return lastPlayerPersistenceState;
    }

    /**
     * Method that sends the current player's bookshelf to the client
     * @param out the printWriter
     * @param gameID the game ID
     * @param nickname the nickname
     */
    public void sendBookshelf(PrintWriter out, int gameID,String nickname){
        out.println(6); //ROWS
        out.println(5); //COLUMS
        List<Player> playerList = controller.get(gameID).getGame().getPlayers();
        Player player = null;
        for (Player p : playerList) {
            if (p.getNickname().equals(nickname)) {
                player = p;
                break;
            }
        }
        Gson gson = new Gson();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                ItemTile tile = player.getBookshelf().getBookshelf()[i][j];
                String json = gson.toJson(tile);
                out.println(json);
            }
        }
    }

    /**
     * Method that sends the token still available for the common goals
     * @param out the printWriter
     * @param gameID the game ID
     */
    public void sendCommonGoalPoints(PrintWriter out, int gameID)
    {
        List<CommonGoalCard> commonGoals = controller.get(gameID).getGame().getCommonGoals();
        out.println(commonGoals.size());
        for(int i = 0; i < commonGoals.size(); i++){
            int points = commonGoals.get(i).getFirstTokenAvailable();
            out.println(points);
        }
    }

    /**
     * Method that send all the players' bookshelves
     * @param out the printWriter
     * @param gameID the game ID
     */
    public void sendAllBookshelves(PrintWriter out, int gameID)
    {
        out.println(6);  //ROWS
        out.println(5);  //COLUMNS
        int players = controller.get(gameID).getNumberOfPlayers();
        out.println(players);
        Gson gson = new Gson();
        for(int k=0; k<players; k++){
            out.println(controller.get(gameID).getGame().getPlayers().get(k).getNickname());
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    ItemTile tile = controller.get(gameID).getGame().getPlayers().get(k).getBookshelf().getBookshelf()[i][j];
                    String json = gson.toJson(tile);
                    out.println(json);
                }
            }
        }
    }

    /**
     * This method is used in order to send to the client each player's score
     * @param out the printWriter
     * @param gameID the game ID
     */
    public void sendScores(PrintWriter out, int gameID)
    {
        List<Player> PlayerList= controller.get(gameID).getGame().getPlayers();
        int NumOfPlayers = controller.get(gameID).getNumberOfPlayers();
        out.println(NumOfPlayers);
        for(Player p: PlayerList){
            out.println(p.getNickname());
            out.println(p.getFinalScore());
        }

    }

    /**
     * Method for sending to the client the nickname of the winner
     * @param out is the PrintWriter used to communicate with the client (sending string)
     * @param gameID the game ID
     */
    public void sendWinner(PrintWriter out, int gameID)
    {
        out.println(controller.get(gameID).getGame().getWinner().getNickname());
    }

    /**
     * Method that disconnects all the players from a game if an issue is presented
     * @param gameID the game ID
     * @param gameName the game name
     */
    public void disconnectAllGamePlayers(int gameID, String gameName)
    {
        lockAddNewGame.lock();

        if(controller.containsKey(gameID)) {

            if(disconnectionHandlersRMI.get(gameID).size() > 0) {
                for (DisconnectionHandlerRMI disconnectionHandler : disconnectionHandlersRMI.get(gameID)) {
                    try {
                        disconnectionHandler.playerHasDisconnected();
                    } catch (RemoteException ignored) {
                        System.out.println("Disconnecting a RMI client...");
                    }
                }
            }

            if (disconnectionHandlerSocket.get(gameID).size() > 0) {
                for (Socket socket : disconnectionHandlerSocket.get(gameID)) {
                    try {
                        socket.close();
                    } catch (IOException ignored) {
                        System.out.println("Disconnecting a socket client... ");
                    }
                }
            }

            try {
                controller.get(gameID).removeFilePersistence();
            } catch (IOException e) {
                System.err.println("Error while writing on file");
            }
            removeParameters(gameID, gameName);
        }

        lockAddNewGame.unlock();
    }

    /**
     * Method that removes all the parameters of a game from all the lists
     * @param gameID the game ID
     * @param gameName the game name
     */
    void removeParameters(int gameID, String gameName)
    {
        if(controller.containsKey(gameID)) {
            controller.remove(gameID);
            semaphoreNumberOfPlayers.remove(gameID);
            semaphoreTurns.remove(gameID);
            waitAllPlayersLock.remove(gameID);
            gameNamesID.remove(gameName);
            disconnectionHandlersRMI.remove(gameID);
            disconnectionHandlerSocket.remove(gameID);
            takenTilesRMI.remove(gameID);
            playerHasPrintedInformationTurn.remove(gameID);
        }
    }

    /**
     * Method that gets the position of the player "nickname" and checks if the information of the
     * turn have already been printed
     * @param gameID the game ID
     * @param nickname the nickname
     * @param out the printWriter
     */
    public void playerHasAlreadyPrintedInformationTurn(int gameID, String nickname, PrintWriter out)
    {
        for(Player player: controller.get(gameID).getGame().getPlayers())
        {
            if(player.getNickname().equals(nickname))
            {
                int placement = player.getPlacement();
                if(playerHasPrintedInformationTurn.get(gameID).get(placement) == false)
                {
                    playerHasPrintedInformationTurn.get(gameID).set(placement, true);
                    out.println(false);
                    printAllInformation(gameID, out,nickname);
                }
                else
                {
                    out.println(true);
                }
            }
        }
    }

    /**
     * Method that gets the value of the attribute PlayerHasPrintedInformationTurn from a player
     * @param gameID the game ID
     * @param nickname the nickname
     * @return the value of the attribute, or false if the player is not in the game
     */
    public boolean getPlayerHasPrintedInformationTurn(int gameID, String nickname)
    {
        for(Player player: controller.get(gameID).getGame().getPlayers())
        {
            if(player.getNickname().equals(nickname))
            {
                int placement = player.getPlacement();
                return playerHasPrintedInformationTurn.get(gameID).get(placement);
            }
        }
        return false;
    }

    /**
     * Method that sends the player on turn
     * @param gameID the game ID
     * @param out the printWriter
     */
    public void sendCurrentPlayer(int gameID,PrintWriter out){
        out.println(controller.get(gameID).getPlayerOnTurn().getNickname());
    }

    /**
     * Method that sends all the view information,included the board,the Bookshelf,other players Bookshelves
     * the common goal and the personal goal
     * @param gameID the game ID
     * @param out the printWriter
     * @param nickname the nickname
     */
    public void printAllInformation(int gameID, PrintWriter out,String nickname)
    {
        sendCurrentPlayer(gameID,out);
        sendBoard(out, gameID);
        sendAllBookshelves(out, gameID);
        sendCommonGoalPoints(out, gameID);
        sendCommonGoalObjective(out,gameID);
        sendPersonalGoalObjective(out,nickname,gameID);
        sendBookshelf(out,gameID,nickname);
    }
}
