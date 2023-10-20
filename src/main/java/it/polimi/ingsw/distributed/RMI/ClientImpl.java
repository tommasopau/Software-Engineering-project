package it.polimi.ingsw.distributed.RMI;

import it.polimi.ingsw.distributed.RMI.Disconnection.DisconnectionHandlerRMI;
import it.polimi.ingsw.distributed.RMI.Disconnection.DisconnectionHandlerRMIImpl;
import it.polimi.ingsw.distributed.Messages.MessagesType;
import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;
import it.polimi.ingsw.model.ItemTile;
import it.polimi.ingsw.util.ClientObserver;
import it.polimi.ingsw.util.UpdateManagerImpl;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * The RMI client
 */
public class ClientImpl {
    private View view;
    private static String ip;
    private static int port;
    private List<Boolean> commonGoalAchieved = new ArrayList<>(); //For printing that a player has finished a common goal card only one time
    private ClientObserver updateManager;
    private boolean alreadyPrintedGameInformation = false;
    String nickAll;

    /**
     * Constructor of class ClientImpl
     *
     * @param ip   of the server
     * @param port of the server
     * @param view the view
     * @throws RemoteException related to RMI
     */
    public ClientImpl(String ip, int port, View view) throws RemoteException {
        super();
        this.ip = ip;
        this.port = port;
        this.view = view;
    }

    /**
     * Method called when a new client join
     *
     * @param port the port
     * @throws RemoteException related to RMI
     * @throws NotBoundException related to RMI
     * @throws InterruptedException related to RMI
     * @throws URISyntaxException related to RMI
     */
    public void startClient(int port) throws NotBoundException, InterruptedException, IOException, URISyntaxException {
        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            String remoteObjectName = "server";
            Server server = (Server) registry.lookup(remoteObjectName); //Interface to server

            view.startNewGame();

            server.printClientInformation(this.ip, this.port);

            List<String> availableGames = new ArrayList<>();
            availableGames.addAll(server.getGameNames());
            List<Integer> numberOfPlayersJoined = new ArrayList<>();
            numberOfPlayersJoined.addAll(server.getNumberOfPlayersJoined());
            List<Integer> numberOfPlayersGame = new ArrayList<>();
            numberOfPlayersGame.addAll(server.getNumberOfPlayersGame());
            view.printAvailableGames(availableGames, numberOfPlayersJoined, numberOfPlayersGame);

            String gameName = view.askGameName();
            server.askGameName(gameName);

            SocketRMIMessages gameWasAlreadyCreatedPersistence = server.gameWasAlreadyCreatedPersistence(gameName);
            view.printMessage(gameWasAlreadyCreatedPersistence);

            ClientObserver updateManager = new UpdateManagerImpl();
            server.askUpdateManager(updateManager, gameName);
            this.updateManager = updateManager;

            String nickname;
            int j = 3;
            int numberOfPlayers = 2; //Default value
            boolean numberOfPlayersNeeded = server.numberOfPlayersNeeded(gameName);
            SocketRMIMessages nickMessage;

            //Case new game
            if (gameWasAlreadyCreatedPersistence.getMessagesType() == MessagesType.NEW_GAME_PERSISTENCE) {

                do {
                    nickname = view.askNickname(); //Asking nickname to client
                    if (numberOfPlayersNeeded == true) //If it is the first player joining...
                    {
                        numberOfPlayers = view.askNumberOfPlayers();
                    }
                    server.addNewPlayer(nickname, numberOfPlayers, gameName); //Trying to add a new player to the game

                    nickMessage = new SocketRMIMessages(updateManager.getUpdate(j).getMessagesType());

                    view.printMessage(nickMessage);

                    if (nickMessage.getMessagesType() == MessagesType.NICKNAME_FULL_GAME) //Case game is full
                    {
                        server.removeUpdateManager(updateManager, gameName);
                        System.exit(1);
                    }

                } while (nickMessage.getMessagesType() != MessagesType.NICKNAME_CORRECT);
            }
            //Case game was already created
            else {
                SocketRMIMessages nicknameMessage = new SocketRMIMessages(MessagesType.START);

                do {
                    nickname = view.askNickname();
                    MessagesType messagesType = server.addPlayerGamePersistence(nickname, gameName).getMessagesType();
                    nicknameMessage = new SocketRMIMessages(messagesType);

                    view.printMessage(nicknameMessage);

                    if (messagesType == MessagesType.NICKNAME_FULL_GAME) {
                        server.removeUpdateManager(updateManager, gameName);
                        System.exit(1);
                    }
                } while (nicknameMessage.getMessagesType() != MessagesType.NICKNAME_CORRECT);
            }

            DisconnectionHandlerRMI disconnectionHandlerRMI = new DisconnectionHandlerRMIImpl(view);
            server.askDisconnectionHandler(disconnectionHandlerRMI, gameName);

            nickAll = nickname;

            view.printAllPlayersJoining();

            server.waitAllPlayers(gameName); //Waiting that all players join...

            view.printAllPlayersHaveJoined();

            printTurns(server, gameName); //Printing turns of the game

            view.printWaitingForYourTurn();

            for (int i = 0; i < server.getNumberOfCommonGoals(gameName); i++) {
                commonGoalAchieved.add(false);
            }

            while (server.gameHasFinished(gameName) == false) //Playing turns until the game has finished
            {
                playTurn(server, nickname, gameName);
            }

            view.printTheGameHasEnded();

            printScores(server, gameName); //Printing scores of the game

            printWinner(server, gameName); //Printing the winner of the game

            server.removeParameters(gameName);

            System.exit(1);
        } catch (RemoteException e) {
            view.printDisconnection(0);
        }
    }

    /**
     * Method for printing turns of the game
     *
     * @param server the RMI server
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    public void printTurns(Server server, String gameName) throws RemoteException {
        List<String> turnsNickname = new ArrayList<>();
        turnsNickname.addAll(server.getTurns(gameName));

        view.printTurns(turnsNickname);
    }

    /**
     * Method called when a player wants to play a turn
     *
     * @param server the RMI Server
     * @param nickname the nickname of the player
     * @param gameName the game name
     * @throws RemoteException related to RMI
     * @throws InterruptedException related to RMI
     * @throws URISyntaxException related to RMI
     */
    public void playTurn(Server server, String nickname, String gameName) throws IOException, InterruptedException, URISyntaxException {
        if (server.isPlayerTurn(nickname, gameName, alreadyPrintedGameInformation) == false) //If it's not the turn of the player or game has finished
        {
            if(server.playerHasAlreadyPrintedInformationTurn(gameName, nickname) == false)
            {
                printAllInformation(server, gameName, nickname);
            }
            return;
        }
        else //If it is player turn
        {
            view.isPlayerTurn();
            extractOrderPlace(server, nickname, gameName);
            alreadyPrintedGameInformation = false;
        }
    }

    /**
     * Taking the tiles from the board, ordering them and finally placing in the bookshelf
     *
     * @param server the RMI Server
     * @param nickname the nickname of the player
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    public void extractOrderPlace(Server server, String nickname, String gameName) throws RemoteException {
        int numberOfTilesWanted = 0;
        List<Integer> coordinates = new ArrayList<>();
        List<Integer> xCoordinates = new ArrayList<>(); //Row asked
        List<Integer> yCoordinates = new ArrayList<>(); //Column asked
        int w = 0;
        int j = 1;
        int k = 2;

        int numberOfRowsBoard = server.getNumberOfRowsBoard();
        int numberOfColumnsBoard = server.getNumberOfColumnsBoard();

        String lastPlayerStatePersistence = server.getLastPlayerState(gameName);

        SocketRMIMessages takeMessage;
        SocketRMIMessages orderMessage= new SocketRMIMessages(MessagesType.START);
        SocketRMIMessages placeMessage= new SocketRMIMessages(MessagesType.START);

        if (lastPlayerStatePersistence.equals("START")) {
            do {
                numberOfTilesWanted = view.askNumberOfTilesWanted();

                coordinates.addAll(view.askCoordinates(numberOfTilesWanted, numberOfRowsBoard, numberOfColumnsBoard));

                for (int i = 0; i < numberOfTilesWanted; i++) {
                    xCoordinates.add(coordinates.get(2 * i));
                    yCoordinates.add(coordinates.get((2 * i) + 1));
                }

                server.takeTiles(xCoordinates, yCoordinates, nickname, gameName); //Message with the results of take tiles

                takeMessage = new SocketRMIMessages(updateManager.getUpdate(w).getMessagesType());

                view.printMessage(takeMessage);

                coordinates.clear();
                xCoordinates.clear();
                yCoordinates.clear();
            } while (takeMessage.getMessagesType() != MessagesType.CORRECT_TAKE);
        }
        coordinates.clear();
        xCoordinates.clear();
        yCoordinates.clear();

        List<ItemTile> takenTiles = new ArrayList<>();
        takenTiles.addAll(server.getTakenTiles(gameName)); //Receiving the tiles asked

        view.printTakenTiles(takenTiles);

        if (lastPlayerStatePersistence.equals("TAKEN") || lastPlayerStatePersistence.equals("START")) {
            if (takenTiles.size() > 1) { //Ordering tiles only if number of asked tile is > 1
                if (takenTiles.size() != 2 || takenTiles.get(0).getItemTileType() != takenTiles.get(1).getItemTileType()) {
                    if (numberOfTilesWanted != 3 || !(takenTiles.get(0).getItemTileType() == takenTiles.get(1).getItemTileType() && takenTiles.get(1).getItemTileType() == takenTiles.get(2).getItemTileType())) {
                        List<Integer> orderTiles = new ArrayList<>();

                        while (orderMessage.getMessagesType() != MessagesType.CORRECT_ORDER_VALUES) {

                            orderTiles.addAll(view.askOrderTile(takenTiles.size()));

                            server.orderTiles(takenTiles, orderTiles, gameName);

                            orderMessage = new SocketRMIMessages(updateManager.getUpdate(j).getMessagesType());
                            view.printMessage(orderMessage);

                            orderTiles.clear();
                        }

                        takenTiles.clear();
                        takenTiles.addAll(server.getTakenTiles(gameName)); //Receiving the ordered tiles
                    }
                }
            }
        }

        printPersonalBookshelf(server, nickname, gameName);

        int numberOfColumns = server.getNumberOfColumnsBookshelf();
        while (placeMessage.getMessagesType() != MessagesType.CORRECT_VALUE_PLACE) { //Placing the tiles
            int columnWanted = -1;

            columnWanted = view.askColumnPlaceTile(numberOfColumns);

            server.placeTiles(nickname, takenTiles, columnWanted, gameName);

            placeMessage = new SocketRMIMessages(updateManager.getUpdate(k).getMessagesType());

            view.printMessage(placeMessage);
        }

        for (int i = 0; i < server.getNumberOfCommonGoals(gameName); i++) //Checking if the player has finished the common goal card
        {
            if (commonGoalAchieved.get(i) == false) {
                if (server.playerHasFinishedCommonGoal(nickname, i, gameName) == true) {
                    view.completedCommonGoalCard(i);
                    commonGoalAchieved.set(i, true);
                }
            }
        }

        if (server.playerHasFinishedBookshelf(nickname, gameName) == true) //Checking if the player has filled the bookshelf
        {
            view.playerHasFullBookshelf();
        } else {
            view.printWaitingForYourTurn();
        }
    }

    /**
     * Printing the bookshelf, given a nickname
     *
     * @param server the RMI Server
     * @param nickname the nickname of the player
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    public void printPersonalBookshelf(Server server, String nickname, String gameName) throws RemoteException {
        int numberOfRowsBookshelf = server.getNumberOfRowsBookshelf();
        int numberOfColumnsBookshelf = server.getNumberOfColumnsBookshelf();
        String[][] personalBookshelf = new String[numberOfRowsBookshelf][numberOfColumnsBookshelf];

        for (int i = 0; i < numberOfRowsBookshelf; i++) {
            for (int j = 0; j < numberOfColumnsBookshelf; j++) {
                personalBookshelf[i][j] = server.getBookshelfItemTile(nickname, i, j, gameName).getCharacter();
            }
        }

        view.printThisIsYourBookShelf();
        view.printPersonalBookshelf(personalBookshelf, numberOfRowsBookshelf, numberOfColumnsBookshelf);
    }

    /**
     * Printing scores of all players
     *
     * @param server the RMI Server
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    public void printScores(Server server, String gameName) throws RemoteException {
        HashMap<String, Integer> scores = server.getScores(gameName);

        view.printScores(scores);
    }

    /**
     * Printing the winner of the game
     *
     * @param server the RMI Server
     * @param gameName the game name
     * @throws RemoteException related to RMI
     */
    public void printWinner(Server server, String gameName) throws RemoteException {
        String winner = server.getWinner(gameName);

        view.printWinner(winner);
    }

    /**
     * Method that creates all the elements needed to use the same method in the view
     * @param server  the RMI Server
     * @param gameName the game name
     * @param nicknamePlayer the nickname of the player
     * @throws IOException error converting an Integer
     * @throws URISyntaxException related to RMI
     */
    public void printAllInformation(Server server, String gameName, String nicknamePlayer) throws IOException, URISyntaxException {

        String currentNickname = server.getCurrentPlayer(gameName);
        int numberOfRowsBoard = server.getNumberOfRowsBoard();
        int numberOfColumnsBoard = server.getNumberOfColumnsBoard();
        ItemTile[][] board = new ItemTile[numberOfRowsBoard][numberOfColumnsBoard];

        for (int i = 0; i < numberOfRowsBoard; i++) {
            for (int j = 0; j < numberOfColumnsBoard; j++) {
                board[i][j] = server.getBoardItemTile(i, j, gameName);
            }
        }


        List<ItemTile[][]> Bookshelves = new ArrayList<>();
        List<ItemTile[][]> newBook = new ArrayList<>();
        List<String> nicknames = new ArrayList<>();
        int numberOfRowsBookshelf = server.getNumberOfRowsBookshelf();
        int numberOfColumnsBookshelf = server.getNumberOfColumnsBookshelf();
        int numberOfPlayers = server.getNumberOfPlayers(gameName);
        ItemTile[][] personalBookshelf = new ItemTile[numberOfRowsBookshelf][numberOfColumnsBookshelf];

        String nick1;
        String nick2;
        String nick3;
        String nick4;
        for (int k = 0; k < numberOfPlayers; k++) {
            String nickname = server.getNicknamePlayerNumber(k, gameName);

            for (int i = 0; i < numberOfRowsBookshelf; i++) {
                for (int j = 0; j < numberOfColumnsBookshelf; j++) {
                    personalBookshelf[i][j] = server.getBookshelfItemTile(nickname, i, j, gameName);
                }
            }
            if (k == 0) {
                ItemTile[][] bookshelf1 = new ItemTile[personalBookshelf.length][];
                for (int i = 0; i < personalBookshelf.length; i++) {
                    bookshelf1[i] = personalBookshelf[i].clone();
                }
                Bookshelves.add(bookshelf1);
                nick1 = nickname;
                nicknames.add(nick1);
                if(!nickAll.equals(nickname)){
                    newBook.add(bookshelf1);
                }
            }
            if (k == 1) {
                ItemTile[][] bookshelf2 = new ItemTile[personalBookshelf.length][];
                for (int i = 0; i < personalBookshelf.length; i++) {
                    bookshelf2[i] = personalBookshelf[i].clone();
                }
                Bookshelves.add(bookshelf2);
                nick2 = nickname;
                nicknames.add(nick2);
                if(!nickAll.equals(nickname)){
                    newBook.add(bookshelf2);
                }
            }
            if (k == 2) {
                ItemTile[][] bookshelf3 = new ItemTile[personalBookshelf.length][];
                for (int i = 0; i < personalBookshelf.length; i++) {
                    bookshelf3[i] = personalBookshelf[i].clone();
                }

                Bookshelves.add(bookshelf3);
                nick3 = nickname;
                nicknames.add(nick3);
                if(!nickAll.equals(nickname)){
                    newBook.add(bookshelf3);
                }
            }
            if (k == 3) {
                ItemTile[][] bookshelf4 = new ItemTile[personalBookshelf.length][];
                for (int i = 0; i < personalBookshelf.length; i++) {
                    bookshelf4[i] = personalBookshelf[i].clone();
                }
                Bookshelves.add(bookshelf4);
                nick4 = nickname;
                nicknames.add(nick4);
                if(!nickAll.equals(nickname)){
                    newBook.add(bookshelf4);
                }
            }



        }
        int numberOfCommonGoalCards = server.getNumberOfCommonGoals(gameName);
        List<Integer> commonGoalTokens = new ArrayList<>();
        for (int i = 0; i < numberOfCommonGoalCards; i++) {
            commonGoalTokens.add(server.getFirstTokenCommonGoal(i, gameName));
        }
        List<String> commonGoalCardsDescription = new ArrayList<>();
        commonGoalCardsDescription.addAll(server.getCommonGoalCardsDescription(gameName));


        String[][] personalGoalCard1 = new String[numberOfRowsBookshelf][numberOfColumnsBookshelf];

        for (int i = 0; i < numberOfRowsBookshelf; i++) {
            for (int j = 0; j < numberOfColumnsBookshelf; j++) {
                personalGoalCard1[i][j] = server.getPersonalGoalItemTile(nicknamePlayer, i, j, gameName).getCharacter();
            }
        }


        ItemTile[][] myBookshelf = new ItemTile[numberOfRowsBookshelf][numberOfColumnsBookshelf];

        for (int i = 0; i < numberOfRowsBookshelf; i++) {
            for (int j = 0; j < numberOfColumnsBookshelf; j++) {
                myBookshelf[i][j] = server.getBookshelfItemTile(nicknamePlayer, i, j, gameName);
            }
        }
        view.printAllInformation(currentNickname, board,numberOfRowsBoard, numberOfColumnsBoard, numberOfRowsBookshelf, numberOfColumnsBookshelf, Bookshelves, commonGoalCardsDescription,commonGoalTokens, nicknames,personalGoalCard1,myBookshelf,newBook);



    }


    }
