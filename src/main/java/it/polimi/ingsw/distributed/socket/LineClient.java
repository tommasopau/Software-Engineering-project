package it.polimi.ingsw.distributed.socket;

import com.google.gson.Gson;
import it.polimi.ingsw.distributed.Messages.MessagesType;
import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;
import it.polimi.ingsw.model.ItemTile;
import it.polimi.ingsw.view.View;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The client if he chooses to use socket
 */
public class LineClient {
    private static String ip;
    private static int port;
    private View view;
    //For printing that a player has finished a common goal card only one time
    private List<Boolean> commonGoalAchieved = new ArrayList<>();
    private String nickAll;


    /**
     * Constructor of class LineClient
     *
     * @param ip   of the server
     * @param port of the server
     * @param view the view
     */
    public LineClient(String ip, int port, View view) {
        this.ip = ip;
        this.port = port;
        this.view = view;
    }

    /**
     * Method for starting the connection
     *
     * @throws IOException related to an error while reading or writing on file
     */
    public void startClient() throws IOException {
        System.out.println("Connecting to the server...");
        try (Socket socket = new Socket(ip, port)) {
            String rem = socket.getRemoteSocketAddress().toString();
            String loc = socket.getLocalSocketAddress().toString();
            System.out.format("Server: %s%n", rem);
            System.out.format("Client: %s%n", loc);

            run(socket);
        } catch (UnknownHostException e) {
            System.err.format("Server name not valid: " + e.getMessage());
        } catch (IOException e) {
            System.err.format("Error during the communication with the server: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("\nA player or the server has disconnected, ending the game...");
        }
    }

    /**
     * Method called when a new client join
     *
     * @param socket
     */
    public void run(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        ) {
            //PHASE 1: starting a new game (join of a new player, waiting for all players).

            view.startNewGame();

            printAvailableGames(in);

            String gameName = view.askGameName();
            out.println(gameName);

            SocketRMIMessages gameWasAlreadyCreatedPersistence = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));
            view.printMessage(gameWasAlreadyCreatedPersistence);

            //Case game was not created before server crash
            if (gameWasAlreadyCreatedPersistence.getMessagesType() == MessagesType.NEW_GAME_PERSISTENCE) {
                SocketRMIMessages correctNickname = new SocketRMIMessages(MessagesType.START);
                while (correctNickname.getMessagesType() != MessagesType.NICKNAME_CORRECT) {
                    String nickname = view.askNickname();
                    out.println(nickname); //Sending nickname to Server
                    nickAll = nickname;

                    int numberOfPlayers = 2; //Default value

                    SocketRMIMessages numberOfPlayersNeeded = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));

                    if (numberOfPlayersNeeded.getMessagesType() == MessagesType.NUMBER_OF_PLAYERS_NEEDED) //If the client is the first player joining
                    {
                        SocketRMIMessages correctNumberOfPlayers = new SocketRMIMessages(MessagesType.NUMBER_OF_PLAYERS_NOT_VALID);
                        while (correctNumberOfPlayers.getMessagesType() == MessagesType.NUMBER_OF_PLAYERS_NOT_VALID) {
                            numberOfPlayers = view.askNumberOfPlayers();
                            out.println(numberOfPlayers);
                            correctNumberOfPlayers = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));
                            if (correctNumberOfPlayers.getMessagesType() == MessagesType.NUMBER_OF_PLAYERS_NOT_VALID) {
                                view.printMessage(correctNumberOfPlayers);
                            }
                        }
                    }
                    correctNickname = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));
                    if (correctNickname.getMessagesType() == MessagesType.NICKNAME_FULL_GAME) {
                        view.printMessage(correctNickname);
                        return;
                    } else {
                        view.printMessage(correctNickname);
                    }
                }
            }
            //Case game was already created before server crash
            else {
                SocketRMIMessages nicknameMessage = new SocketRMIMessages(MessagesType.START);

                do {
                    String nickname = view.askNickname();
                    out.println(nickname);
                    nickAll = nickname;
                    nicknameMessage = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));

                    view.printMessage(nicknameMessage);

                    if (nicknameMessage.getMessagesType() == MessagesType.NICKNAME_FULL_GAME) {
                        return;
                    }
                } while (nicknameMessage.getMessagesType() != MessagesType.NICKNAME_CORRECT);
            }

            view.printAllPlayersJoining();
            in.readLine();

            view.printAllPlayersHaveJoined();

            for (int i = 0; i < 2; i++) {
                commonGoalAchieved.add(false);
            }

            //Printing the turns
            printTurns(in);

            //END OF PHASE 1
            //PHASE 2: the player chooses what to do (pick the tiles or observe the bookshelves/board)

            SocketRMIMessages gameHasNotFinished = new SocketRMIMessages(MessagesType.GAME_HAS_NOT_FINISHED);
            while (in.readLine().equals(gameHasNotFinished.getMessagesType().toString())) {
                playTurn(in, out);
            }

            //END OF PHASE 2
            //PHASE 3: end of the game

            view.printTheGameHasEnded();
            //Printing the final scores
            printScores(in);
            //Printing the winner
            printWinner(in);

        } catch (UnsupportedEncodingException e) {
            System.err.println("Codification of characters not supported: " + e.getMessage());
        } catch (IOException | NullPointerException e) {
            view.printDisconnection(2);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e.getMessage());
        }
    }

    /**
     * Asking server all the available games and how many players have joined
     *
     * @param in  is the BufferedReader used to communicate with the client (reading strings)
     */
    public void printAvailableGames(BufferedReader in) {
        try {
            List<String> availableGames = new ArrayList<>();
            List<Integer> numberOfPlayersJoined = new ArrayList<>();
            List<Integer> numberOfPlayersGame = new ArrayList<>();

            int numberOfGamesAvailable = Integer.parseInt(in.readLine());

            for (int i = 0; i < numberOfGamesAvailable; i++) {
                availableGames.add(in.readLine());
                numberOfPlayersJoined.add(Integer.parseInt(in.readLine()));
                numberOfPlayersGame.add(Integer.parseInt(in.readLine()));
            }

            view.printAvailableGames(availableGames, numberOfPlayersJoined, numberOfPlayersGame);

        } catch (IOException | NullPointerException e) {
            view.printDisconnection(2);
        }
    }

    /**
     * Method for sending to the view the players' turn (the sequence in which the players will play)
     *
     * @param in is the BufferedReader used to read strings from the server
     * @throws IOException related to an error while reading or writing on file
     * @throws ClassNotFoundException
     */
    public void printTurns(BufferedReader in) throws IOException, ClassNotFoundException {
        List<String> turnsNicknames = new ArrayList<>();
        int numberOfPlayers = Integer.parseInt(in.readLine());
        for (int i = 0; i < numberOfPlayers; i++) {
            turnsNicknames.add(in.readLine());
        }
        view.printTurns(turnsNicknames);
    }

    /**
     * Checking if it is player's turn, else he waits for his turn
     * @param in  is the BufferedReader used to communicate with the client (reading strings)
     * @param out is the PrintWriter used to communicate with the client (sending strings)
     */
    public void playTurn(BufferedReader in, PrintWriter out) {
        try {
            SocketRMIMessages isPlayerTurn = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));
            if (isPlayerTurn.getMessagesType() == MessagesType.NOT_PLAYER_ON_TURN) { //Case not player on turn
                boolean playerHasPrintedInformationTurn = Boolean.parseBoolean(in.readLine());
                if (playerHasPrintedInformationTurn == false) {
                    view.printWaitingForYourTurn();
                    printAllInformation(in);
                }
                return;
            }
            if (isPlayerTurn.getMessagesType() == MessagesType.PLAYER_ON_TURN) {
                //Case player on turn
                view.isPlayerTurn();
                extractOrderPlace(in, out);
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Codification of characters not supported: " + e.getMessage());
        } catch (IOException | NullPointerException e) {
            view.printDisconnection(2);
        }
    }

    /**
     * Taking the tiles from the board, ordering them and finally placing in the bookshelf
     *
     * @param in  is the BufferedReader used to communicate with the client (reading strings)
     * @param out is the PrintWriter used to communicate with the client (sending strings)
     */
    public void extractOrderPlace(BufferedReader in, PrintWriter out) {
        try {
            int numberOfTilesWanted;
            List<Integer> coordinates = new ArrayList<>();
            SocketRMIMessages messagesTakeTiles;

            String lastPlayerStatePersistence = in.readLine();

            if (lastPlayerStatePersistence.equals("START")) {

                //Checking if the choice of the player is correct
                do {
                    //Checking if there are columns with a number of spaces >= of the number of tiles wanted from the player
                    do {
                        numberOfTilesWanted = view.askNumberOfTilesWanted();
                        out.println(numberOfTilesWanted);
                        messagesTakeTiles = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));
                        if (messagesTakeTiles.getMessagesType() == MessagesType.NOT_ENOUGH_SPACE_TAKE) {
                            view.printMessage(messagesTakeTiles);
                        }
                    } while (messagesTakeTiles.getMessagesType() == MessagesType.NOT_ENOUGH_SPACE_TAKE);

                    coordinates.addAll(view.askCoordinates(numberOfTilesWanted, 9, 9));
                    List<Integer> xCoordinates = new ArrayList<>();
                    List<Integer> yCoordinates = new ArrayList<>();
                    for (int i = 0; i < numberOfTilesWanted; i++) {
                        xCoordinates.add(coordinates.get(2 * i));
                        yCoordinates.add(coordinates.get((2 * i) + 1));
                    }
                    for (int i = 0; i < numberOfTilesWanted; i++) {
                        out.println(xCoordinates.get(i));
                    }
                    for (int i = 0; i < numberOfTilesWanted; i++) {
                        out.println(yCoordinates.get(i));
                    }
                    messagesTakeTiles = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));
                    view.printMessage(messagesTakeTiles);
                    coordinates.clear();
                } while (messagesTakeTiles.getMessagesType() != MessagesType.CORRECT_TAKE);
            }

            //Showing the tiles that the player took
            List<ItemTile> takenTiles = new ArrayList<>();
            numberOfTilesWanted = Integer.parseInt(in.readLine());
            Gson gson = new Gson();
            for (int i = 0; i < numberOfTilesWanted; i++) {
                String json = in.readLine();
                ItemTile tile = gson.fromJson(json, ItemTile.class);
                takenTiles.add(tile);
            }
            view.printTakenTiles(takenTiles);

            if (lastPlayerStatePersistence.equals("TAKEN") || lastPlayerStatePersistence.equals("START")) {
                //Choosing the order
                if (numberOfTilesWanted > 1) { //Ordering tiles only if number of asked tile is > 1
                    if ((numberOfTilesWanted != 2 || takenTiles.get(0).getItemTileType() != takenTiles.get(1).getItemTileType())) {
                        if (numberOfTilesWanted != 3 || !(takenTiles.get(0).getItemTileType() == takenTiles.get(1).getItemTileType() && takenTiles.get(1).getItemTileType() == takenTiles.get(2).getItemTileType())) {
                            List<Integer> order = new ArrayList<>();
                            SocketRMIMessages messagesOrder;
                            do {
                                order.addAll(view.askOrderTile(numberOfTilesWanted));
                                for (int i = 0; i < numberOfTilesWanted; i++) {
                                    out.println(order.get(i));
                                }
                                messagesOrder = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));
                                view.printMessage(messagesOrder);
                                order.clear();
                            } while (messagesOrder.getMessagesType() != MessagesType.CORRECT_ORDER_VALUES);
                        }
                    }
                }
            }

            printPersonalBookshelf(in);

            //Placing tiles
            int column;
            SocketRMIMessages messagesPlace;
            do {
                column = view.askColumnPlaceTile(5);
                out.println(column);
                messagesPlace = new SocketRMIMessages(MessagesType.valueOf(in.readLine()));
                view.printMessage(messagesPlace);
            } while (messagesPlace.getMessagesType() != MessagesType.CORRECT_VALUE_PLACE);

            //Possible achievement of a Common Goal
            for (int i = 0; i < commonGoalAchieved.size(); i++) {
                if (Integer.parseInt(in.readLine()) != 0 && !commonGoalAchieved.get(i)) {
                    view.completedCommonGoalCard(i);
                    commonGoalAchieved.set(i, true);
                }
            }

            //Possible filling of the bookshelf
            if (MessagesType.valueOf(in.readLine()) == MessagesType.FULL_BOOKSHELF) {
                view.playerHasFullBookshelf();
            } else {
                view.printWaitingForYourTurn();
            }
        } catch (IOException | NullPointerException e) {
            view.printDisconnection(2);
        }
    }

    /**
     * method that sends the current player's bookshelf to the view to be printed
     *
     * @param in the bufferReader
     */
    public void printPersonalBookshelf(BufferedReader in) {
        view.printThisIsYourBookShelf();

        try {
            int rows = Integer.parseInt(in.readLine());
            int columns = Integer.parseInt(in.readLine());
            String[][] personalBookshelf = new String[rows][columns];
            ItemTile[][] cardTile = new ItemTile[rows][columns];
            Gson gson = new Gson();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    String json = in.readLine();
                    ItemTile tile = gson.fromJson(json, ItemTile.class);
                    cardTile[i][j] = tile;
                    personalBookshelf[i][j] = tile.getCharacter();
                }
            }

            view.printPersonalBookshelf(personalBookshelf, rows, columns);


        } catch (IOException | NullPointerException e) {
            view.printDisconnection(2);
        }
    }

    /**
     * This method is used in order to print a Map that shows every player's score.
     *
     * @param in the bufferReader
     */
    public void printScores(BufferedReader in) {
        HashMap<String, Integer> scores = new HashMap<String, Integer>();
        try {
            int NumOfPlayers = Integer.parseInt(in.readLine());
            for (int i = 0; i < NumOfPlayers; i++) {
                String nickname = in.readLine();
                int score = Integer.parseInt(in.readLine());
                scores.put(nickname, score);
            }
            view.printScores(scores);


        } catch (IOException | NullPointerException e) {
            view.printDisconnection(2);
        }

    }


    /**
     * Method for sending to the view the winner
     *
     * @param in is the BufferedReader used to read from the server
     * @throws IOException related to an error while reading or writing on file
     */
    public void printWinner(BufferedReader in) throws IOException {
        view.printWinner(in.readLine());
    }

    /**
     * Method used to read from the server all the information about the game; it sends the board,the player on turn's bookshelf, other
     * players' bookshelf,the personal goal card and the common goal cards.
     *
     * @param in the bufferReader
     */
    public void printAllInformation(BufferedReader in) {
        try {
            String currentP = in.readLine();

            int rows1 = Integer.parseInt(in.readLine());
            int columns1 = Integer.parseInt(in.readLine());
            ItemTile[][] board = new ItemTile[rows1][columns1];

            Gson gson1 = new Gson();
            for (int i = 0; i < rows1; i++) {
                for (int j = 0; j < columns1; j++) {
                    String json = in.readLine();
                    ItemTile tile = gson1.fromJson(json, ItemTile.class);
                    board[i][j] = tile;
                }
            }

            List<ItemTile[][]> Bookshelves = new ArrayList<>();
            List<ItemTile[][]> newBook = new ArrayList<>();
            List<String> nicknames = new ArrayList<>();
            int rows = Integer.parseInt(in.readLine());
            int columns = Integer.parseInt(in.readLine());
            int players = Integer.parseInt(in.readLine());
            ItemTile[][] bookshelf = new ItemTile[rows][columns];


            String nick1;
            String nick2;
            String nick3;
            String nick4;


            Gson gson = new Gson();
            for (int k = 0; k < players; k++) {
                String nickname = in.readLine();


                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        String json = in.readLine();
                        ItemTile tile = gson.fromJson(json, ItemTile.class);
                        bookshelf[i][j] = tile;
                    }
                }
                if (k == 0) {
                    ItemTile[][] bookshelf1 = new ItemTile[bookshelf.length][];
                    for (int i = 0; i < bookshelf.length; i++) {
                        bookshelf1[i] = bookshelf[i].clone();
                    }
                    Bookshelves.add(bookshelf1);
                    nick1 = nickname;
                    nicknames.add(nick1);
                    if(!nickname.equals(nickAll)){
                        newBook.add(bookshelf1);
                    }
                }
                if (k == 1) {
                    ItemTile[][] bookshelf2 = new ItemTile[bookshelf.length][];
                    for (int i = 0; i < bookshelf.length; i++) {
                        bookshelf2[i] = bookshelf[i].clone();
                    }
                    Bookshelves.add(bookshelf2);
                    nick2 = nickname;
                    nicknames.add(nick2);
                    if(!nickname.equals(nickAll)){
                        newBook.add(bookshelf2);
                    }
                }
                if (k == 2) {
                    ItemTile[][] bookshelf3 = new ItemTile[bookshelf.length][];
                    for (int i = 0; i < bookshelf.length; i++) {
                        bookshelf3[i] = bookshelf[i].clone();
                    }

                    Bookshelves.add(bookshelf3);
                    nick3 = nickname;
                    nicknames.add(nick3);
                    if(!nickname.equals(nickAll)){
                        newBook.add(bookshelf3);
                    }
                }
                if (k == 3) {
                    ItemTile[][] bookshelf4 = new ItemTile[bookshelf.length][];
                    for (int i = 0; i < bookshelf.length; i++) {
                        bookshelf4[i] = bookshelf[i].clone();
                    }
                    Bookshelves.add(bookshelf4);
                    nick4 = nickname;
                    nicknames.add(nick4);
                    if(!nickname.equals(nickAll)){
                        newBook.add(bookshelf4);
                    }
                }


            }


            int numberOfCards = Integer.parseInt(in.readLine());
            List<Integer> tokensList = new ArrayList<>();
            for (int i = 0; i < numberOfCards; i++) {
                int points = Integer.parseInt(in.readLine());
                tokensList.add(points);
            }


            List<String> descriptions = new ArrayList<>();
            int numberOfCommonGoals = Integer.parseInt(in.readLine());
            for (int i = 0; i < numberOfCommonGoals; i++) {
                descriptions.add(in.readLine());
            }
            int rowsp = Integer.parseInt(in.readLine());
            int columnp = Integer.parseInt(in.readLine());
            String[][] personalGoalCard = new String[rowsp][columnp];
            ItemTile[][] cardTile = new ItemTile[rowsp][columnp];
            Gson gsonp = new Gson();
            for (int i = 0; i < rowsp; i++) {
                for (int j = 0; j < columnp; j++) {
                    String jsonp = in.readLine();
                    ItemTile tilep = gsonp.fromJson(jsonp, ItemTile.class);
                    cardTile[i][j] = tilep;
                    personalGoalCard[i][j] = tilep.getCharacter();
                }
            }
            int rowsx = Integer.parseInt(in.readLine());
            int columnsx = Integer.parseInt(in.readLine());
            String[][] personalBookshelf = new String[rowsx][columnsx];
            ItemTile[][] cardTilex = new ItemTile[rowsx][columnsx];
            Gson gsonx = new Gson();
            for (int i = 0; i < rowsx; i++) {
                for (int j = 0; j < columnsx; j++) {
                    String jsonx = in.readLine();
                    ItemTile tilex = gsonx.fromJson(jsonx, ItemTile.class);
                    cardTilex[i][j] = tilex;
                    personalBookshelf[i][j] = tilex.getCharacter();
                }
            }


            view.printAllInformation(currentP, board, rows1, columns1, rows, columns, Bookshelves, descriptions, tokensList, nicknames, personalGoalCard, cardTilex, newBook);

        } catch (IOException | NullPointerException | URISyntaxException e) {
            view.printDisconnection(2);
        } catch (NumberFormatException ignored)
        {

        }


    }
}
