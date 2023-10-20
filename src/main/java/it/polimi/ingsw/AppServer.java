package it.polimi.ingsw;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.distributed.RMI.Disconnection.DisconnectionHandlerRMI;
import it.polimi.ingsw.distributed.RMI.ServerImpl;
import it.polimi.ingsw.distributed.socket.MultiServer;
import it.polimi.ingsw.exceptions.RowAndColumnNotFoundException;
import it.polimi.ingsw.model.ItemTile;
import it.polimi.ingsw.model.ItemTileType;
import it.polimi.ingsw.model.PersonalGoalCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.util.ClientObserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The application to run for running the server
 */
public class AppServer {

    static final int maxThread = 4;
    static ExecutorService executor = Executors.newFixedThreadPool(maxThread);
    static Lock lock = new ReentrantLock();
    static HashMap<Integer, Controller> controller = new HashMap<>();
    static HashMap<Integer, Semaphore> semaphoreNumberOfPlayers = new HashMap<>(); //For management of first player joining
    static HashMap<Integer, Semaphore> semaphoreTurns = new HashMap<>(); //For management of turns
    static HashMap<Integer, Object> waitAllPlayersLock = new HashMap<>(); //For waiting all players after they joined
    static Lock lockAddNewGame = new ReentrantLock(); //For management of new games
    static HashMap<String, Integer> gameNamesID = new HashMap<>(); //For tracking the game ID based on its unique name
    static AtomicInteger gameIDGlobal = new AtomicInteger(); //For setting an ID to a new game
    static HashMap<Integer, List<DisconnectionHandlerRMI>> disconnectionHandlersRMI = new HashMap<>(); //For management of disconnections for RMI
    static HashMap<Integer, List<Socket>> disconnectionHandlerSocket = new HashMap<>(); //For management of disconnections for socket
    static HashMap<Integer, List<ItemTile>> takenTilesRMI = new HashMap<>();
    static HashMap<Integer, AtomicInteger> allPlayersHavePrintEndGame = new HashMap<>();
    static HashMap<Integer, List<ClientObserver>> updateManagers = new HashMap<>();
    static HashMap<Integer, List<Boolean>> playerHasPrintedInformationTurn = new HashMap<>();

    /**
     * main
     * @param args 0: server ip. 1: RMI port. 2: socket port
     */
    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", args[0]);

        try {
            Integer.parseInt(args[1]);
            Integer.parseInt(args[2]);
        }catch (NumberFormatException e)
        {
            System.err.println("You inserted a port that is not a number");
            return;
        }

        gameIDGlobal.set(0);
        addExistingGamesPersistence();

        Thread rmiThread = new Thread() {
            @Override
            public void run() {
                try {
                    startRMI(Integer.parseInt(args[1]));
                } catch (RemoteException e) {
                    System.err.println("Cannot start RMI. This protocol will be disabled.");
                } catch (AlreadyBoundException e) {
                    System.err.println("Already Bound Exception");
                }
            }
        };

        rmiThread.start();

        Thread socketThread = new Thread() {
            @Override
            public void run() {
                try {
                    startSocket(Integer.parseInt(args[2]));
                } catch (RemoteException e) {
                    System.err.println("Cannot start socket. This protocol will be disabled.");
                }
            }
        };

        socketThread.start();

        try {
            rmiThread.join();
            socketThread.join();
        } catch (InterruptedException e) {
            System.err.println("No connection protocol available. Exiting...");
        }
    }

    /**
     * Method for starting RMI server
     * @param port port chosen
     * @throws AlreadyBoundException related to RMI
     * @throws RemoteException related to RMI
     */
    public static void startRMI(int port) throws AlreadyBoundException, RemoteException {
        lock.lock();
        System.out.println("Constructing server RMI implementation...");
        ServerImpl server = new ServerImpl(port, controller, semaphoreNumberOfPlayers, semaphoreTurns, waitAllPlayersLock,
                lockAddNewGame, gameNamesID, gameIDGlobal, disconnectionHandlersRMI, disconnectionHandlerSocket, takenTilesRMI,
                allPlayersHavePrintEndGame, updateManagers, playerHasPrintedInformationTurn);
        System.out.println("Binding server implementation to registry...");
        Registry registry = LocateRegistry.createRegistry(server.port);
        registry.bind("server", server);
        System.out.println("Waiting for invocations from clients...\n\n");
        lock.unlock();
    }

    /**
     * Method for starting RMI server
     * @param port port chosen
     @throws RemoteException related to RMI
     */
    public static void startSocket(int port) throws RemoteException {
        try(ServerSocket server = new ServerSocket(port))
        {
            lock.lock();
            System.out.println("Server socket listening on: " + server.getLocalSocketAddress());
            lock.unlock();

            while (true)
            {
                Socket tempSck;
                try {
                    tempSck = server.accept();
                    executor.execute(() -> {
                        Socket client = tempSck;
                        try(client)
                        {
                            String rem = client.getRemoteSocketAddress().toString();
                            Thread t = Thread.currentThread();
                            System.out.println("Thread Socket " + t.getId() + " - Client (remote): " + rem);

                            MultiServer multiServer = new MultiServer(port,  controller, semaphoreNumberOfPlayers,
                                    semaphoreTurns, waitAllPlayersLock, lockAddNewGame, gameNamesID, gameIDGlobal,
                                    disconnectionHandlersRMI, disconnectionHandlerSocket, takenTilesRMI,
                                    allPlayersHavePrintEndGame, updateManagers, playerHasPrintedInformationTurn);

                            multiServer.start(client);
                        } catch (IOException e) {
                            System.err.println("Error during the communication with the client: " + e.getMessage());
                        }
                    });
                } catch (IOException e) {
                    System.err.println("Error during the generation of new connections: " + e.getMessage());
                }
            }
        }catch (IOException e){
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Method that checks if there are any available games on disk
     */
    public static void addExistingGamesPersistence()
    {
        //Creating directory
        String fileNameDirectory = System.getProperty("user.dir") + "/Persistence";
        File fileDirectory = new File(fileNameDirectory);
        if(fileDirectory.exists() == false)
        {
            fileDirectory.mkdirs();
        }

        String fileNameGameNames = System.getProperty("user.dir") + "/Persistence/GameNames.txt";
        File fileGameNames = new File(fileNameGameNames);
        if(fileGameNames.exists() == false)
        {
            try {
                fileGameNames.createNewFile();
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }
        try {
            Scanner scanner = new Scanner(fileGameNames);
            //Checking if there are any available games
            while (scanner.hasNextLine())
            {
                String gameName = scanner.nextLine();
                gameNamesID.put(gameName, gameIDGlobal.get());
                Controller controllerGame = new Controller();
                controller.put(gameIDGlobal.get(), controllerGame);
                controllerGame.setGameWasAlreadyCreated(true);
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
                allPlayersHavePrintEndGame.put(gameIDGlobal.get(), atomicInteger);
                List<ClientObserver> updateManagersGame = new ArrayList<>();
                updateManagers.put(gameIDGlobal.get(), updateManagersGame);
                List<Boolean> playerHasPrintedInformationTurnGame = new ArrayList<>();
                playerHasPrintedInformationTurn.put(gameIDGlobal.get(), playerHasPrintedInformationTurnGame);

                addGamePersistence(gameName);
                removeTilesFromBagPersistence(gameName);
                setBoardPersistence(gameName);
                addTileBookshelfPersistence(gameName);
                setCommonGoalCardsGamePersistence(gameName);
                setPersonalGoalCardsPersistence(gameName);
                setPlayerOnTurnPersistence(gameName);
                setTilesTakenPersistence(gameName);

                gameIDGlobal.set(gameIDGlobal.get() + 1);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Impossible to find a file");
        }
    }

    /**
     * Method for saving the information about the game
     * @param gameName the game name
     */
    public static void addGamePersistence(String gameName)
    {
        String fileNameGame = System.getProperty("user.dir") + "/Persistence/" + gameName + "Game.txt";
        File fileGame = new File(fileNameGame);
        try {
            //Number of players
            Scanner scanner = new Scanner(fileGame);
            int numberOfPlayers = Integer.parseInt(scanner.nextLine());
            controller.get(gameIDGlobal.get()).setNumberOfPlayers(numberOfPlayers);
            controller.get(gameIDGlobal.get()).createGame(numberOfPlayers);

            //Nicknames of players
            for(int i = 0; i < numberOfPlayers; i++)
            {
                Player player = new Player(scanner.nextLine());
                controller.get(gameIDGlobal.get()).getGame().addPlayer(player);
            }

            //Order of players
            for(int i = 0; i < numberOfPlayers; i++)
            {
                controller.get(gameIDGlobal.get()).getOrderedPlayers().add(Integer.parseInt(scanner.nextLine()));
            }

            //Checking if a player has finished
            if(scanner.hasNextLine())
            {
                controller.get(gameIDGlobal.get()).getGame().setFinalTokenTaken(true);
                String nicknamePlayerHasFinishedFirst = scanner.nextLine();
                for(Player player : controller.get(gameIDGlobal.get()).getGame().getPlayers())
                {
                    if(player.getNickname().equals(nicknamePlayerHasFinishedFirst) == true)
                    {
                        player.setFinalToken(true);
                    }
                }
            }
            controller.get(gameIDGlobal.get()).getGame().setGameName(gameName);
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error while writing on file");
        }
    }

    /**
     * Removing tiles that were removed in previous game
     * @param gameName the game name
     */
    public static void removeTilesFromBagPersistence(String gameName)
    {
        String fileNameGame = System.getProperty("user.dir") + "/Persistence/" + gameName + "Bag.txt";
        File fileGame = new File(fileNameGame);
        try {
            Scanner scanner = new Scanner(fileGame);
            while (scanner.hasNextLine())
            {
                ItemTileType itemTileType = ItemTileType.valueOf(scanner.nextLine());
                int type = Integer.parseInt(scanner.nextLine());
                ItemTile itemTile = new ItemTile(itemTileType, type);
                controller.get(gameIDGlobal.get()).getGame().getGameBoard().getBag().removeTile(itemTile);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Impossible to find a file");
        }
    }

    /**
     * Setting the board as the same as the board before the crash
     * @param gameName the game name
     */
    public static void setBoardPersistence(String gameName)
    {
        String fileNameGame = System.getProperty("user.dir") + "/Persistence/" + gameName + "Board.txt";
        File fileGame = new File(fileNameGame);
        try {
            Scanner scanner = new Scanner(fileGame);
            for(int i = 0; i < 9; i++)
            {
                for(int j = 0; j < 9; j++)
                {
                    ItemTileType itemTileType = ItemTileType.valueOf(scanner.nextLine());
                    int type = Integer.parseInt(scanner.nextLine());
                    ItemTile itemTile = new ItemTile(itemTileType, type);
                    controller.get(gameIDGlobal.get()).getGame().getGameBoard().setTile(itemTile, i, j);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Impossible to find a file");
        }
    }

    /**
     * Adding tiles that were added before the crash of the server
     * @param gameName the game name
     */
    public static void addTileBookshelfPersistence(String gameName)
    {
        String fileNameGame = System.getProperty("user.dir") + "/Persistence/" + gameName + "Bookshelf.txt";
        File fileGame = new File(fileNameGame);
        if(fileGame.exists()) {
            try {
                Scanner scanner = new Scanner(fileGame);
                while (scanner.hasNextLine()) {
                    String nickname = scanner.nextLine();
                    ItemTileType itemTileType = ItemTileType.valueOf(scanner.nextLine());
                    int type = Integer.parseInt(scanner.nextLine());
                    ItemTile itemTile = new ItemTile(itemTileType, type);
                    int column = Integer.parseInt(scanner.nextLine());
                    for (Player player : controller.get(gameIDGlobal.get()).getGame().getPlayers()) {
                        if (player.getNickname().equals(nickname)) {
                            player.getBookshelf().addTile(itemTile, column);
                        }
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                System.err.println("Impossible to find a file");
            } catch (RowAndColumnNotFoundException e) {
                System.err.println("Row or column not found exception");
            }
        }
    }

    /**
     * Setting the common goal cards and if anyone has completed them
     * @param gameName the game name
     */
    public static void setCommonGoalCardsGamePersistence(String gameName)
    {
        String fileNameGame = System.getProperty("user.dir") + "/Persistence/" + gameName + "CommonGoalCard.txt";
        File fileGame = new File(fileNameGame);
        try {
            Scanner scanner = new Scanner(fileGame);
            for(int i = 0; i < 2; i++)
            {
                int commonID = Integer.parseInt(scanner.nextLine());
                controller.get(gameIDGlobal.get()).getGame().addCommonGoalPersistence(commonID);
            }

            for(Player player : controller.get(gameIDGlobal.get()).getGame().getPlayers())
            {
                for(int i = 0; i < 2; i++)
                {
                    player.getCommonScores().set(i, 0);

                }
            }

            while (scanner.hasNextLine())
            {
                String nickname = scanner.nextLine();
                int commonID = Integer.parseInt(scanner.nextLine());
                int points = controller.get(gameIDGlobal.get()).getGame().getCommonGoals().get(commonID).getOnTop();
                for (Player player : controller.get(gameIDGlobal.get()).getGame().getPlayers())
                {
                    if (player.getNickname().equals(nickname))
                    {
                        player.getCommonScores().set(commonID, points);
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Impossible to find a file");
        }
    }

    /**
     * Setting the personal goal cards
     * @param gameName the game name
     */
    public static void setPersonalGoalCardsPersistence(String gameName)
    {
        String fileNameGame = System.getProperty("user.dir") + "/Persistence/" + gameName + "PersonalGoalCard.txt";
        File fileGame = new File(fileNameGame);
        try {
            Scanner scanner = new Scanner(fileGame);
            for(int i = 0; i < controller.get(gameIDGlobal.get()).getNumberOfPlayers(); i++)
            {
                int personalId = Integer.parseInt(scanner.nextLine());
                PersonalGoalCard personalGoalCard = new PersonalGoalCard(personalId);
                controller.get(gameIDGlobal.get()).getGame().getPlayers().get(i).setPersonalGoalCard(personalGoalCard);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (URISyntaxException | IOException e) {
            System.err.println("Error while writing on file");
        }
    }

    /**
     * Getting the last player that was playing his turn and which move has already made
     * @param gameName the game name
     */
    public static void setPlayerOnTurnPersistence(String gameName)
    {
        String fileNameGame = System.getProperty("user.dir") + "/Persistence/" + gameName + "PlayerOnTurn.txt";
        File fileGame = new File(fileNameGame);
        try {
            Scanner scanner = new Scanner(fileGame);
            int playerOnTurn = Integer.parseInt(scanner.nextLine());
            String lastPlayerStatus = scanner.nextLine();
            controller.get(gameIDGlobal.get()).setPlayerOnTurn(playerOnTurn);
            controller.get(gameIDGlobal.get()).setLastPlayerPersistenceState(lastPlayerStatus);
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Impossible to find a file");
        }
    }

    /**
     * Getting the tiles that were taken from the last player on turn
     * @param gameName the game name
     */
    public static void setTilesTakenPersistence(String gameName) {
        if (!controller.get(gameIDGlobal.get()).getLastPlayerPersistenceState().equals("START")) {
            String fileNameGame = System.getProperty("user.dir") + "/Persistence/" + gameName + "TilesTaken.txt";
            File fileGame = new File(fileNameGame);
            if(fileGame.exists()) {
                try {
                    Scanner scanner = new Scanner(fileGame);
                    while (scanner.hasNextLine()) {
                        ItemTileType itemTileType = ItemTileType.valueOf(scanner.nextLine());
                        int type = Integer.parseInt(scanner.nextLine());
                        ItemTile itemTile = new ItemTile(itemTileType, type);
                        takenTilesRMI.get(gameIDGlobal.get()).add(itemTile);
                    }
                    scanner.close();
                } catch (FileNotFoundException e) {
                    System.err.println("Impossible to find a file");
                }
            }
        }
    }
}
