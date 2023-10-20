package it.polimi.ingsw.distributed.RMI;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.distributed.RMI.Disconnection.DisconnectionHandlerRMI;
import it.polimi.ingsw.distributed.Messages.MessagesType;
import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.CommonGoalCard;
import it.polimi.ingsw.model.ItemTile;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.util.ClientObserver;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The RMI Server
 */
public class ServerImpl extends UnicastRemoteObject implements Server {

    /**
     * The server port
     */
    public int port;
    HashMap<Integer, Controller> controller = new HashMap<>();
    HashMap<Integer, Semaphore> semaphoreNumberOfPlayers = new HashMap<>(); //For management of first player joining
    HashMap<Integer, Semaphore> semaphoreTurns = new HashMap<>(); //For management of turns
    HashMap<Integer, Object> waitAllPlayersLock = new HashMap<>();
    HashMap<Integer, List<ItemTile>> takenTilesRMI = new HashMap<>(); //For management of take tiles and order tiles
    HashMap<Integer, List<DisconnectionHandlerRMI>> disconnectionHandlersRMI = new HashMap<>(); //For managing disconnections
    HashMap<Integer, List<Socket>> disconnectionHandlerSocket = new HashMap<>();
    HashMap<String, Integer> gameNamesID = new HashMap<>();
    AtomicInteger gameIDGlobal;
    Lock lockAddNewGame = new ReentrantLock();
    HashMap<Integer, AtomicInteger> allPlayersHavePrintEndGame;
    HashMap<Integer, List<ClientObserver>> updateManagers = new HashMap<>();
    HashMap<Integer, List<Boolean>> playerHasPrintedInformationTurn = new HashMap<>();

    /**
     * Constructor for ServerImpl
     *
     * @param port the port
     * @param controller hashMap of controllers
     * @param semaphoreNumberOfPlayers hashMap of semaphore for number of players
     * @param semaphoreTurns hashMap of semaphore of turns
     * @param waitPlayersLock hashMap of all players lock
     * @param lockAddNewGame Lock for creation of new game
     * @param gameNamesID ID for the games
     * @param gameIDGlobal ID for new games
     * @param disconnectionHandlersRMI hashMap of disconnection handler RMI
     * @param disconnectionHandlerSocket hashMap of disconnection handler Socket
     * @param takenTilesRMI hashMap of tiles taken
     * @param allPlayersHavePrintEndGame hashMap for all players have printed end game
     * @param updateManagers hashMap for update managers
     * @param playerHasPrintedInformationTurn hashMap for player has printed information turn
     * @throws RemoteException related to RMI
     */
    public ServerImpl(int port, HashMap<Integer, Controller> controller, HashMap<Integer,Semaphore> semaphoreNumberOfPlayers,
                      HashMap<Integer, Semaphore> semaphoreTurns, HashMap<Integer, Object> waitPlayersLock, Lock lockAddNewGame,
                      HashMap<String, Integer> gameNamesID, AtomicInteger gameIDGlobal,
                      HashMap<Integer, List<DisconnectionHandlerRMI>> disconnectionHandlersRMI,
                      HashMap<Integer, List<Socket>> disconnectionHandlerSocket, HashMap<Integer, List<ItemTile>> takenTilesRMI,
                      HashMap<Integer, AtomicInteger> allPlayersHavePrintEndGame, HashMap<Integer, List<ClientObserver>> updateManagers,
                      HashMap<Integer, List<Boolean>> playerHasPrintedInformationTurn) throws RemoteException
    {
        this.port = port;
        this.controller = controller;
        this.semaphoreNumberOfPlayers = semaphoreNumberOfPlayers;
        this.semaphoreTurns = semaphoreTurns;
        this.waitAllPlayersLock = waitPlayersLock;
        this.gameNamesID = gameNamesID;
        this.lockAddNewGame = lockAddNewGame;
        this.gameIDGlobal = gameIDGlobal;
        this.disconnectionHandlersRMI = disconnectionHandlersRMI;
        this.disconnectionHandlerSocket = disconnectionHandlerSocket;
        this.takenTilesRMI = takenTilesRMI;
        this.allPlayersHavePrintEndGame = allPlayersHavePrintEndGame;
        this.updateManagers = updateManagers;
        this.playerHasPrintedInformationTurn = playerHasPrintedInformationTurn;
        System.out.println("RMI server listening on port" + port);
    }

    /**
     * Method that prints the connection attributes of this client
     * @param clientIP ip of client
     * @param clientPort port of client
     */
    @Override
    public void printClientInformation(String clientIP, int clientPort)
    {
        System.out.println("Thread RMI - Client (remote): " + clientIP + ": " + clientPort);
    }

    /**
     * Method that adds a new client observer to the list of update managers
     * @param clientObserver the client observer
     * @param gameName the game name
     */
    @Override
    public void askUpdateManager(ClientObserver clientObserver, String gameName) {
        int gameID = gameNamesID.get(gameName);

        updateManagers.get(gameID).add(clientObserver);
    }

    /**
     * Method that removes the client observer from the update managers list
     * @param clientObserver the client observer
     * @param gameName the game name
     */
    public void removeUpdateManager(ClientObserver clientObserver, String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        updateManagers.get(gameID).remove(clientObserver);
    }

    /**
     *Method that adds a new disconnection handler for the game. During the game it checks for disconnections
     * and handles the disconnection of the other players
     * @param disconnectionHandlerRMI the disconnection handler for RMI
     * @param gameName the game name
     */
    @Override
    public void askDisconnectionHandler(DisconnectionHandlerRMI disconnectionHandlerRMI, String gameName) {
        int gameID = gameNamesID.get(gameName);
        AtomicInteger countdown = new AtomicInteger(5);

        disconnectionHandlersRMI.get(gameID).add(disconnectionHandlerRMI);

        Thread threadDisconnection = new Thread() {
            @Override
            public void run() {
                ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
                AtomicBoolean flag = new AtomicBoolean(true);

                executorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            disconnectionHandlerRMI.waitDisconnectionLoop();
                            flag.set(true);
                        } catch (InterruptedException e) {

                        } catch (RemoteException e) {

                        }
                    }
                }, 0, 2000, TimeUnit.MILLISECONDS);

                while (true) {
                    if (flag.get() == false) {
                        if(countdown.get() == 0)
                        {
                            break;
                        }
                        else
                        {
                            countdown.set(countdown.get() - 1);
                        }
                    }
                    else
                    {
                        countdown.set(5);
                    }
                    flag.set(false);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {

                    }
                }
                lockAddNewGame.lock();

                if (controller.containsKey(gameID)) {

                    for (DisconnectionHandlerRMI disconnectionHandler : disconnectionHandlersRMI.get(gameID)) {
                        try {
                            disconnectionHandler.playerHasDisconnected();
                        } catch (RemoteException ignored) {
                            System.out.println("Disconnecting a RMI client...");
                        }
                    }

                    if (disconnectionHandlerSocket.get(gameID).size() > 0) {
                        for (Socket socket : disconnectionHandlerSocket.get(gameID)) {
                            try {
                                socket.close();
                                System.out.println("Disconnecting a socket client... ");
                            } catch (IOException ignored) {

                            }
                        }
                    }

                    try {
                        controller.get(gameID).removeFilePersistence();
                    } catch (IOException e) {
                        System.err.println("Error while removing file");
                    }
                    controller.remove(gameID);
                    semaphoreNumberOfPlayers.remove(gameID);
                    semaphoreTurns.remove(gameID);
                    waitAllPlayersLock.remove(gameID);
                    gameNamesID.remove(gameName);
                    disconnectionHandlersRMI.remove(gameID);
                    disconnectionHandlerSocket.remove(gameID);
                    takenTilesRMI.remove(gameID);
                    updateManagers.remove(gameID);
                    playerHasPrintedInformationTurn.remove(gameID);
                }

                lockAddNewGame.unlock();
            }
        };

        threadDisconnection.start();
    }

    /**
     * Method that iterates on the controller to obtain the names of all the games that are being played
     * @return a lst on strings containing the game names
     */
    @Override
    public List<String> getGameNames()
    {
        lockAddNewGame.lock();

        List<String> gameNames = new ArrayList<>();
        for(Integer controllerID : controller.keySet())
        {
            if(controller.get(controllerID).getGame() != null) {
                gameNames.add(controller.get(controllerID).getGame().getGameName());
            }
        }

        lockAddNewGame.unlock();

        return gameNames;
    }

    /**
     * Method that checks in the controllers for the number of players connected to each one of them
     * @return a list of integers containing the number of players
     */
    @Override
    public List<Integer> getNumberOfPlayersJoined()
    {
        lockAddNewGame.lock();

        List<Integer> numberOfPlayersJoined = new ArrayList<>();

        for(Integer controllerID : controller.keySet())
        {
            if(controller.get(controllerID).getGame() != null) {
                numberOfPlayersJoined.add(controller.get(controllerID).getNumberOfPlayersConnected());
            }
        }

        lockAddNewGame.unlock();

        return numberOfPlayersJoined;
    }

    /**
     * Method that checks for the number of players expected to let every game start
     * @return a list of integer containing the numbers
     */
    @Override
    public List<Integer> getNumberOfPlayersGame()
    {
        lockAddNewGame.lock();

        List<Integer> numberOfPlayersJoined = new ArrayList<>();

        for(Integer controllerID : controller.keySet())
        {
            if(controller.get(controllerID).getGame() != null) {
                numberOfPlayersJoined.add(controller.get(controllerID).getNumberOfPlayers());
            }
        }

        lockAddNewGame.unlock();

        return numberOfPlayersJoined;
    }

    /**
     * Method that tries to add a new game in case it has a different gameName then the rest or the list is empty
     * @param gameName the game name
     */
    @Override
    public void askGameName(String gameName) {
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
            List<DisconnectionHandlerRMI> disconnectionHandlersRMIGame = new ArrayList<>();
            disconnectionHandlersRMI.put(gameIDGlobal.get(), disconnectionHandlersRMIGame);
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
    }

    /**
     * Method that checks if a game was already present in the list related to persistence
     * @param gameName game name
     * @return a message with the answer
     */
    @Override
    public SocketRMIMessages gameWasAlreadyCreatedPersistence(String gameName)
    {
        int gameID = gameNamesID.get(gameName);
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
     * Returns true if it is the first player, so the player can insert how many players will play the game
     * @return true if it is the first player
     * @throws InterruptedException related to RMI
     */
    @Override
    public boolean numberOfPlayersNeeded(String gameName) throws InterruptedException {
        int gameID = gameNamesID.get(gameName);

        semaphoreNumberOfPlayers.get(gameID).acquire();
        if(controller.get(gameID).getGame() == null)
        {
            return true;
        }
        else
        {
            semaphoreNumberOfPlayers.get(gameID).release();
            return false;
        }
    }

    /**
     * Client ask to be added to the game. He inserts his nickname and the numberOfPlayer that will play the game
     * @param nickname the nickname
     * @param numberOfPlayers the number of players
     * @throws RemoteException related to RMI
     */
    @Override
    public void addNewPlayer(String nickname, int numberOfPlayers, String gameName) throws RemoteException {
        int gameID = gameNamesID.get(gameName);
        int i = 3;
        boolean ret = false;

        synchronized (waitAllPlayersLock.get(gameID)) {
            if (numberOfPlayers < 2 || numberOfPlayers > 4) {
                //Case number of players not valid
                SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NUMBER_OF_PLAYERS_NOT_VALID);
                notifyObservers(returnMessage, i, gameName);
            }
            try {
                controller.get(gameID).addNewPlayer(nickname, numberOfPlayers);
                controller.get(gameID).getGame().setGameName(gameName);
                playerHasPrintedInformationTurn.get(gameID).add(false);
                if (semaphoreNumberOfPlayers.get(gameID).availablePermits() == 0) {
                    //This is called only if it is the first player
                    semaphoreNumberOfPlayers.get(gameID).release();
                }
                waitAllPlayersLock.get(gameID).notifyAll();
                //Notify all threads that are waiting that all players join
                SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NICKNAME_CORRECT);
                notifyObservers(returnMessage, i, gameName);
            } catch (FullGameException e) {
                //Case game is full
                SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NICKNAME_FULL_GAME);
                notifyObservers(returnMessage, i, gameName);

            } catch (NicknameTooLongException e) {
                //Case name is too long
                SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NICKNAME_TOO_LONG);
                notifyObservers(returnMessage, i, gameName);
            } catch (NicknameAlreadyTakenException e) {
                //Case name is already taken
                SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NICKNAME_TAKEN);
                notifyObservers(returnMessage, i, gameName);

            } catch (NicknameNotValidCharactersException e) {
                //Case nickname doesn't contain any alphabet or number
                SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NICKNAME_NOT_VALID_CHARACTERS);
                notifyObservers(returnMessage, i, gameName);
            } catch (IOException | URISyntaxException e) {
                System.err.println("Error while writing on file");
            }
        }
    }

    /**
     * Method that tries to add a new player to the persistence collection
     * @param nickname the nickname
     * @param gameName the game name
     * @return a message containing a positive answer or an error
     */
    @Override
    public SocketRMIMessages addPlayerGamePersistence(String nickname, String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        synchronized (waitAllPlayersLock.get(gameID)) {
            try {
                controller.get(gameID).addNewPlayerPersistence(nickname);
                playerHasPrintedInformationTurn.get(gameID).add(false);
                waitAllPlayersLock.get(gameID).notifyAll();
                return new SocketRMIMessages(MessagesType.NICKNAME_CORRECT);
            } catch (NicknameAlreadyTakenException e) {
                return new SocketRMIMessages(MessagesType.NICKNAME_TAKEN);
            } catch (NicknameNotFoundPersistenceException e) {
                return new SocketRMIMessages(MessagesType.NICKNAME_NOT_FOUND_PERSISTENCE);
            } catch (FullGameException e) {
                lockAddNewGame.unlock();
                return new SocketRMIMessages(MessagesType.NICKNAME_FULL_GAME);
            }
        }
    }

    /**
     * Method called for waiting that all players join the game
     * @throws RemoteException related to RMI
     * @throws InterruptedException related to RMI
     */
    @Override
    public void waitAllPlayers(String gameName) throws RemoteException, InterruptedException {
        int gameID = gameNamesID.get(gameName);

        synchronized (waitAllPlayersLock.get(gameID)) {
            while (controller.get(gameID).getNumberOfPlayersConnected() != controller.get(gameID).getNumberOfPlayers()) {
                //Thread wait until all players join
                waitAllPlayersLock.get(gameID).wait();
            }
        }
    }

    /**
     * Returns a list with the description of the commonGoals of the game
     * @return a list with the description of the commonGoals of the game
     * @throws RemoteException related to RMI
     */
    @Override
    public List<String> getCommonGoalCardsDescription(String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        List<String> commonGoalCardsDescription = new ArrayList<>();
        for(CommonGoalCard commonGoalCard: controller.get(gameID).getGame().getCommonGoals())
        {
            commonGoalCardsDescription.add(commonGoalCard.getDescription());
        }
        return commonGoalCardsDescription;
    }

    /**
     * Returns an ordered list containing the turns of the game (first nickname to last nickname)
     * @return an ordered list containing the turns of the game
     * @throws RemoteException related to RMI
     */
    @Override
    public List<String> getTurns(String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        List<String> turnsNicknames = new ArrayList<>();
        for(Integer orderedPlayer : controller.get(gameID).getOrderedPlayers())
        {
            turnsNicknames.add(controller.get(gameID).getGame().getPlayers().get(orderedPlayer).getNickname());
        }
        return turnsNicknames;
    }

    /**
     * Returns true if the game has finished
     * @return true if the game has finished
     */
    @Override
    public boolean gameHasFinished(String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        if(controller.get(gameID).getPlayerOnTurn() == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns true if it is the turn of the player, given a nickname
     * @param nickname the nickname
     * @return true if it is the turn of the player
     * @throws InterruptedException related to RMI
     */
    @Override
    public boolean isPlayerTurn(String nickname, String gameName, boolean playerHasPrintedInformation) throws InterruptedException {
        int gameID = gameNamesID.get(gameName);

        if(getPlayerHasPrintedInformationTurn(gameName, nickname) == false)
        {
            return false;
        }
        semaphoreTurns.get(gameID).acquire(); //A player ask if it is its turn, and they wait.
        if(controller.get(gameID).getPlayerOnTurn() == null) //Case game has finished
        {
            semaphoreTurns.get(gameID).release();
            return false;
        }
        else if(getPlayerHasPrintedInformationTurn(gameName, nickname) == false)
        {
            semaphoreTurns.get(gameID).release();
            return false;
        }
        else if(controller.get(gameID).getPlayerOnTurn().getNickname().equals(nickname)) //Case is player on turn
        {
            return true;
        }
        else //Case is not player on turn
        {
            semaphoreTurns.get(gameID).release();
            return false;
        }
    }

    /**
     * Returns a map, containing for each player their final scores
     * @return a map, containing for each player their final scores
     * @throws RemoteException related to RMI
     */
    @Override
    public HashMap<String, Integer> getScores(String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        HashMap<String, Integer> scores = new HashMap<>();
        for(Player player : controller.get(gameID).getGame().getPlayers())
        {
            scores.put(player.getNickname(), player.getFinalScore());
        }
        return scores;
    }

    /**
     * Returns the nickname of the winner
     * @return the nickname of the winner
     * @throws RemoteException related to RMI
     */
    @Override
    public String getWinner(String gameName)
    {
        int gameID = gameNamesID.get(gameName);
        String winner = controller.get(gameID).getGame().getWinner().getNickname();

        synchronized (waitAllPlayersLock.get(gameID)) {
            allPlayersHavePrintEndGame.get(gameID).set(allPlayersHavePrintEndGame.get(gameID).get() + 1);
        }

        return winner;
    }

    /**
     * Returns the number of rows of the bookshelf
     * @return the number of rows of the bookshelf
     */
    @Override
    public int getNumberOfRowsBookshelf()
    {
        return 6;
    }

    /**
     * Returns the number of columns of the bookshelf
     * @return the number of columns of the bookshelf
     */
    @Override
    public int getNumberOfColumnsBookshelf()
    {
        return 5;
    }

    /**
     * Returns a tile of the personal Goal Card of a player, given the position and the nickname
     * @param nickname the nickname
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a tile of the personal Goal Card of a player
     */
    @Override
    public ItemTile getPersonalGoalItemTile(String nickname, int x, int y, String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        for(Player player : controller.get(gameID).getGame().getPlayers())
        {
            if(player.getNickname().equals(nickname))
            {
                return player.getPersonalGoalCard().getPersonalGoalCard()[x][y];
            }
        }
        return null;
    }

    /**
     * Returns a tile of the bookshelf of a player, given the position and the nickname
     * @param nickname nickname
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a tile of the bookshelf
     */
    @Override
    public ItemTile getBookshelfItemTile(String nickname, int x, int y, String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        for(Player player : controller.get(gameID).getGame().getPlayers())
        {
            if(player.getNickname().equals(nickname))
            {
                return player.getBookshelf().getBookshelf()[x][y];
            }
        }
        return null;
    }

    /**
     * Returns the number of rows of the board
     * @return the number of rows of the board
     */
    @Override
    public int getNumberOfRowsBoard()
    {
        return 9;
    }

    /**
     * Returns the number of columns of the board
     * @return the number of columns of the board
     */
    @Override
    public int getNumberOfColumnsBoard()
    {
        return 9;
    }

    /**
     * Return a tile of the board, given the position
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a tile of the board
     */
    @Override
    public ItemTile getBoardItemTile(int x, int y, String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        return controller.get(gameID).getGame().getGameBoard().getTile(x, y);
    }

    /**
     * Returns the number of players of the game
     * @return the number of players of the game
     */
    @Override
    public int getNumberOfPlayers(String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        return controller.get(gameID).getNumberOfPlayers();
    }

    /**
     * Returns the nickname of a player in the list players in Game
     * @param i the index in the list
     * @return the nickname of a player in the list players in Game
     */
    @Override
    public String getNicknamePlayerNumber(int i, String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        return controller.get(gameID).getGame().getPlayers().get(i).getNickname();
    }

    /**
     * Returns the number of common goal cards in the game
     * @return the number of common goal cards in the game
     */
    @Override
    public int getNumberOfCommonGoals(String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        return controller.get(gameID).getGame().getCommonGoals().size();
    }

    /**
     * Returns the first token available for a common goal card
     * @param i the index of common goal
     * @return the first token available for a common goal card
     * @throws RemoteException related to RMI
     */
    @Override
    public int getFirstTokenCommonGoal(int i, String gameName) throws RemoteException {
        int gameID = gameNamesID.get(gameName);

        if(i >= getNumberOfCommonGoals(gameName))
        {
            return 0;
        }
        else
        {
            return controller.get(gameID).getGame().getCommonGoals().get(i).getFirstTokenAvailable();
        }
    }

    /**
     * Method that gets the persistence state of the last player
     * @param gameName the game name
     * @return a string with the information
     */
    @Override
    public String getLastPlayerState(String gameName)
    {
        int gameID = gameNamesID.get(gameName);
        String lastPlayerPersistenceState = controller.get(gameID).getLastPlayerPersistenceState();
        return lastPlayerPersistenceState;
    }

    /**
     * The player, after entering the coordinates, tries to take a tile. Returns a message
     *
     * @param xCoordinates the x coordinate
     * @param yCoordinates the y coordinate
     * @param nickname the nickname
     */
    @Override
    public void takeTiles(List<Integer> xCoordinates, List<Integer> yCoordinates, String nickname, String gameName) throws RemoteException {
        int gameID = gameNamesID.get(gameName);
        int i = 0;

        for(Player player: controller.get(gameID).getGame().getPlayers())
        {
            if(player.getNickname().equals(nickname))
            {
                if(xCoordinates.size() > player.getBookshelf().numberOfPlaceableTiles()) //Case bookshelf has no space for wanted tiles in every column
                {
                    SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NOT_ENOUGH_SPACE_IN_COLUMN);
                    notifyObservers(returnMessage, i, gameName);
                    return;
                }
            }
        }
        try {
            takenTilesRMI.get(gameID).addAll(controller.get(gameID).takeTiles(xCoordinates, yCoordinates));
        } catch (NumberOfTilesNotValidException e) { //Case entered a position outside the board
            SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NUMBER_OF_TILES_WANTED_NOT_VALID);
            notifyObservers(returnMessage, i, gameName);
            return;
        } catch (NotValidConfigurationException e) { //Case asked tiles not in a straight line
            SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NOT_VALID_CONFIGURATION_TAKE);
            notifyObservers(returnMessage, i, gameName);
            return;
        } catch (NotTakeableTileException e) { //Case tiles asked are not takeable
            SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NOT_TAKEABLE_TILE);
            notifyObservers(returnMessage, i, gameName);
            return;
        } catch (DuplicateTakeTilesException e) { //Case asked a tile for more than two times
            SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.DUPLICATE_TAKE_TILE);
            notifyObservers(returnMessage, i, gameName);
            return;
        } catch (NotEnoughTakeableTilesException e) { //Case number of tiles wanted is greater than number of takeable tiles
            SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NOT_ENOUGH_TAKEABLE_TILES);
            notifyObservers(returnMessage, i, gameName);
            return;
        } catch (IOException e) {
            System.err.println("Error while writing on file");
        }
        controller.get(gameID).setLastPlayerPersistenceState("START");
        SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.CORRECT_TAKE);
        notifyObservers(returnMessage, i, gameName);
        return;
    }

    /**
     * Returns the tiles asked, in case taken tiles and ordered tiles
     * @return the tiles asked, in case taken tiles and ordered tiles
     */
    @Override
    public List<ItemTile> getTakenTiles(String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        ArrayList<ItemTile> takenTilesCopy = new ArrayList<>();
        takenTilesCopy.addAll(takenTilesRMI.get(gameID));
        takenTilesRMI.get(gameID).clear();
        return takenTilesCopy;
    }

    /**
     * The player, after entering the order for placing the tiles, tries to order the tiles. Returns a message
     *
     * @param items the tiles
     * @param order the order
     */
    @Override
    public void orderTiles(List<ItemTile> items, List<Integer> order, String gameName) throws RemoteException {
        int gameID = gameNamesID.get(gameName);
        int i = 1;

        try {
            takenTilesRMI.get(gameID).addAll(controller.get(gameID).chooseOrder(items, order));
        } catch (DuplicateNumberOrderException e) { //Case order contains duplicate numbers
            SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.DUPLICATE_ORDER_VALUES);
            notifyObservers(returnMessage, i, gameName);
            return;
        } catch (NotValidNumberOrderException e) { //Case order contains not valid numbers
            SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NOT_VALID_ORDER_VALUES);
            notifyObservers(returnMessage, i, gameName);
            return;
        } catch (IOException e) {
            System.err.println("Error while writing on file");
        }
        controller.get(gameID).setLastPlayerPersistenceState("START");
        SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.CORRECT_ORDER_VALUES);
        notifyObservers(returnMessage, i, gameName);
        return;
    }

    /**
     * The player, after entering the column chosen, tries to place the tiles given in the bookshelf. Returns a message
     *
     * @param nickname the nickname
     * @param items the tiles
     * @param column the column
     */
    @Override
    public void placeTiles(String nickname, List<ItemTile> items, int column, String gameName) throws RemoteException {
        int gameID = gameNamesID.get(gameName);
        int i = 2;

        try {
            for(Player player : controller.get(gameID).getGame().getPlayers()) {
                if(player.getNickname().equals(nickname)) {
                    controller.get(gameID).placeTiles(player, items, column);
                }
            }
        } catch (RowAndColumnNotFoundException e) { //Case column is not a correct value
            SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NOT_CORRECT_VALUE_PLACE);
            notifyObservers(returnMessage, i, gameName);
            return;
        } catch (NotEnoughSpaceInColumnException e) { //Case column is full or doesn't have enough space
            SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.NOT_ENOUGH_SPACE_IN_COLUMN);
            notifyObservers(returnMessage, i, gameName);
            return;
        } catch (IOException e) {
            System.err.println("Error while writing on file");
        }

        for(int j = 0; j < playerHasPrintedInformationTurn.get(gameID).size(); j++)
        {
            playerHasPrintedInformationTurn.get(gameID).set(j, false);
        }

        semaphoreTurns.get(gameID).release(); //Ending the turn

        controller.get(gameID).setLastPlayerPersistenceState("START");
        SocketRMIMessages returnMessage = new SocketRMIMessages(MessagesType.CORRECT_VALUE_PLACE);
        notifyObservers(returnMessage, i, gameName);
        return;
    }

    /**
     * Returns true if a player has finished a common goal card
     * @param nickname the nickname
     * @param numberCommonGoal the common goal card
     * @return true if a player has finished a common goal card
     */
    @Override
    public boolean playerHasFinishedCommonGoal(String nickname, int numberCommonGoal, String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        for(Player player : controller.get(gameID).getGame().getPlayers())
        {
            if(player.getNickname().equals(nickname)) {
                if (player.getCommonScores().get(numberCommonGoal) != 0) {
                    return true;
                } else if (player.getCommonScores().get(numberCommonGoal) == 0) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if a player has finished the bookshelf
     * @param nickname the nickname
     * @return true if a player has finished the bookshelf
     */
    @Override
    public boolean playerHasFinishedBookshelf(String nickname, String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        for(Player player : controller.get(gameID).getGame().getPlayers())
        {
            if(player.getNickname().equals(nickname) && player.getBookshelf().isFull() == true)
            {
                return true;
            }
            else if(player.getNickname().equals(nickname) && player.getBookshelf().isFull() == false)
            {
                return false;
            }
        }
        return false;
    }

    /**
     * Method that removes all the parameters of the passed Game from their lists
     * @param gameName the game name
     * @throws InterruptedException related to RMI
     */
    @Override
    public void removeParameters(String gameName) throws InterruptedException {
        int gameID = gameNamesID.get(gameName);
        synchronized (waitAllPlayersLock.get(gameID)) {
            waitAllPlayersLock.get(gameID).notifyAll();
            while (allPlayersHavePrintEndGame.get(gameID).get() != controller.get(gameID).getNumberOfPlayers()) {
                waitAllPlayersLock.get(gameID).wait();
            }
        }
        synchronized (lockAddNewGame) {
            if (gameNamesID.containsKey(gameName)) {
                if (controller.containsKey(gameID)) {
                    try {
                        controller.get(gameID).removeFilePersistence();
                    } catch (IOException e) {
                        System.err.println("Error while writing on file");
                    }
                    controller.remove(gameID);
                    semaphoreNumberOfPlayers.remove(gameID);
                    semaphoreTurns.remove(gameID);
                    waitAllPlayersLock.remove(gameID);
                    disconnectionHandlersRMI.remove(gameID);
                    disconnectionHandlerSocket.remove(gameID);
                    takenTilesRMI.remove(gameID);
                    gameNamesID.remove(gameName);
                    updateManagers.remove(gameID);
                    playerHasPrintedInformationTurn.remove(gameID);
                }
            }
        }
    }


    /**
     * Method called when a message has to be updated in the observers. Checks the list and calls the
     * method update() in each one of them
     * @param message the message
     * @param i the observer number
     * @param gameName the game name
     * @return when the value is updated
     */
    @Override
    public boolean notifyObservers(SocketRMIMessages message, int i, String gameName) {
        boolean updated = false;
        int gameID = gameNamesID.get(gameName);

        synchronized(updateManagers) {
            List<ClientObserver> observers = new ArrayList<>(updateManagers.get(gameID));
            for (ClientObserver updateManager : observers){
                try {
                    updated = updateManager.update(message, i);
                } catch (RemoteException e) {
                    System.out.println("Disconnecting a RMI client...");
                }
            }
        }
        return updated;
    }

    /**
     * Method that gets the position of the player "nickname" and checks if the information of the
     * turn have already been printed
     * @param gameName the game name
     * @param nickname the nickname
     * @return true if the information were printed before, false if not
     */
    public boolean playerHasAlreadyPrintedInformationTurn(String gameName, String nickname)
    {
        int gameID = gameNamesID.get(gameName);
        for(Player player: controller.get(gameID).getGame().getPlayers())
        {
            if(player.getNickname().equals(nickname))
            {
                int placement = player.getPlacement();
                if(playerHasPrintedInformationTurn.get(gameID).get(placement) == false)
                {
                    playerHasPrintedInformationTurn.get(gameID).set(placement, true);
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method that gets the value of the attribute PlayerHasPrintedInformationTurn from a player
     * @param gameName the game name
     * @param nickname the nickname
     * @return the value of the attribute, or false if the player is not in the game
     */
    public boolean getPlayerHasPrintedInformationTurn(String gameName, String nickname)
    {
        int gameID = gameNamesID.get(gameName);
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
     * Getter for the nickname of the current player
     * @param gameName the game name
     * @return the nickname
     */
    public String getCurrentPlayer(String gameName)
    {
        int gameID = gameNamesID.get(gameName);

        return controller.get(gameID).getPlayerOnTurn().getNickname();
    }


}