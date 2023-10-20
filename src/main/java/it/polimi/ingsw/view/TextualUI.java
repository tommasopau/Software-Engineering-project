package it.polimi.ingsw.view;

import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;
import it.polimi.ingsw.model.ItemTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * The Textual User Interface
 */
public class TextualUI implements View {
    /**
     * Method used at the beginning of the game
     */
    @Override
    public void startNewGame() {
        System.out.println();
        System.out.println("  __  __           ____    _              _    __   _        ");
        System.out.println(" |  \\/  |  _   _  / ___|  | |__     ___  | |  / _| (_)   ___ ");
        System.out.println(" | |\\/| | | | | | \\___ \\  | '_ \\   / _ \\ | | | |_  | |  / _ \\");
        System.out.println(" | |  | | | |_| |  ___) | | | | | |  __/ | | |  _| | | |  __/");
        System.out.println(" |_|  |_|  \\__, | |____/  |_| |_|  \\___| |_| |_|   |_|  \\___|");
        System.out.println("           |___/                                             \n");

    }

    /**
     * Method used in order to show the current games' lobbies
     * @param gameName name of the game
     * @param numberOfPlayersJoined number of players that already joined
     * @param numberOfPlayersGame size of the lobby
     */
    public void printAvailableGames(List<String> gameName, List<Integer> numberOfPlayersJoined, List<Integer> numberOfPlayersGame) {
        if (gameName.size() == 0) {
            System.out.println("There are no available games right now.");
        } else {
            System.out.println("These are the available lobbies in the game: ");
            for (int i = 0; i < gameName.size(); i++) {
                System.out.println("\t" + gameName.get(i) + ": " + numberOfPlayersJoined.get(i) + " / " + numberOfPlayersGame.get(i) + " players");
            }
        }
    }

    /**
     * Method used in order to insert the game name
     * @return the game name
     */
    public String askGameName() {
        Scanner s = new Scanner(System.in);
        System.out.println("\nInsert the game name (if it doesn't exist, a new game will be created): ");
        String gameName = s.next();
        return gameName;
    }

    /**
     * Method for asking the nickname
     * @return the asked nickname
     */
    @Override
    public String askNickname() {
        Scanner s = new Scanner(System.in);
        System.out.println("\nInsert your nickname (max 16 characters): ");
        String nickname = s.next();
        return nickname;
    }

    /**
     * Method for asking the number of players that will join the match
     * @return the asked number of players
     */
    @Override
    public int askNumberOfPlayers() {
        Scanner s = new Scanner(System.in);
        int numberOfPlayers;
        while (true) {
            System.out.println("Insert the number of players that will play the game (2-4): ");
            String numberOfPlayersString = s.next();
            try {
                numberOfPlayers = Integer.parseInt(numberOfPlayersString);
                if (numberOfPlayers >= 2 && numberOfPlayers <= 4) {
                    return numberOfPlayers;
                } else if (numberOfPlayers < 2) {
                    System.err.println("Minimum number of players is 2, try again.");
                } else if (numberOfPlayers > 4) {
                    System.err.println("Maximum number of players is 4, try again.");
                }
            } catch (NumberFormatException e) {
                System.err.println("The number you inserted is not an integer, try again.");
            }
        }
    }

    /**
     * Printing errore message
     * @param message the error message
     */
    @Override
    public void printMessage(SocketRMIMessages message) {
        System.err.println(message.getErrorDescription());
    }

    /**
     * Method used when a player is waiting that all players join the lobby
     */
    @Override
    public void printAllPlayersJoining() {
        System.out.println("Waiting that all players join the game...\n");
    }

    /**
     * Method used when all players have joined the lobby
     */
    @Override
    public void printAllPlayersHaveJoined() {
        System.out.println("All players have joined, the game starts!\n");
    }

    /**
     * Method used to print the personal goal card
     * @param personalGoalCard the personal goal card
     * @param numberOfRowsBookshelf number of rows of the bookshelf
     * @param numberOfColumnsBookshelf number of columns of the bookshelf
     */
    @Override
    public void printPersonalGoalCard(String[][] personalGoalCard, int numberOfRowsBookshelf, int numberOfColumnsBookshelf) {
        System.out.println("\nYour Personal Goal Card:");
        System.out.print("      ");
        for (int i = 0; i < numberOfColumnsBookshelf; i++) {
            System.out.print(i + "   ");
        }
        System.out.println();
        for (int i = 0; i < numberOfRowsBookshelf; i++) {
            System.out.println("    *---*---*---*---*---*");
            for (int j = 0; j < numberOfColumnsBookshelf; j++) {
                if (j == 0) {
                    System.out.print(i + "   | ");
                }
                System.out.print(personalGoalCard[i][j] + " | ");
            }
            System.out.println();
        }
        System.out.println("    *---*---*---*---*---*");
    }

    /**
     * Method used to print the way turns are managed
     * @param turnsNickname the ordered nicknames
     */
    @Override
    public void printTurns(List<String> turnsNickname) {
        System.out.println("\nThis is how turns are managed:");
        for (int i = 0; i < turnsNickname.size(); i++) {
            if (i != turnsNickname.size() - 1) {
                System.out.print(turnsNickname.get(i) + " -> ");
            } else {
                System.out.println(turnsNickname.get(i));
            }
        }
    }

    /**
     * Method used when a player is waiting for his turn
     */
    @Override
    public void printWaitingForYourTurn() {
        System.out.println("\nWaiting your turn...\n");
    }

    /**
     * Method used when it's player's turn
     */
    @Override
    public void isPlayerTurn() {
        System.out.print("\nIt's your turn. ");
    }

    /**
     * Method used to ask the number of tiles that the player wants to get
     * @return the asked number
     */
    @Override
    public int askNumberOfTilesWanted() {
        int numberOfTilesWanted = 0;
        Scanner s = new Scanner(System.in);
        boolean correctChoice = false;

        while (correctChoice == false) { //Asking how many tiles the player wants
            System.out.println("Choose the number of tiles that you want to take from the board (1-3): ");
            try {
                String numberOfTilesWantedString = s.next();
                numberOfTilesWanted = Integer.parseInt(numberOfTilesWantedString);
                if (numberOfTilesWanted >= 1 && numberOfTilesWanted <= 3) {
                    correctChoice = true;
                } else {
                    System.err.println("Number inserted is not valid, try again:");
                }
            } catch (NumberFormatException e) {
                System.err.println("The number you inserted is not an integer, try again.");
            }
        }
        return numberOfTilesWanted;
    }

    /**
     * Method used to ask the coordinates in which the player wants to insert the tiles
     * @param numberOfTilesWanted number of tiles wanted
     * @param numberOfRowsBoard number of rows of the board
     * @param numberOfColumnsBoard number of columns of the board
     * @return the coordinates of the asked tiles
     */
    @Override
    public List<Integer> askCoordinates(int numberOfTilesWanted, int numberOfRowsBoard, int numberOfColumnsBoard) {
        Scanner s = new Scanner(System.in);
        List<Integer> coordinates = new ArrayList<>();
        int rowWanted = -1;
        int columnWanted = -1;
        for (int i = 0; i < numberOfTilesWanted; i++) { //Asking row and column for each tile
            try {
                System.out.print("Tile n° ");
                System.out.print(i + 1);
                System.out.print(", insert the row (0 - ");
                System.out.print(numberOfRowsBoard - 1);
                System.out.println("): ");
                String rowWantedString = s.next();
                rowWanted = Integer.parseInt(rowWantedString);
                System.out.print("Tile n° ");
                System.out.print(i + 1);
                System.out.print(", insert the column (0 - ");
                System.out.print(numberOfColumnsBoard - 1);
                System.out.println("): ");
                String columnWantedString = s.next();
                columnWanted = Integer.parseInt(columnWantedString);
                if (rowWanted >= numberOfRowsBoard || columnWanted >= numberOfColumnsBoard) {
                    System.err.println("Coordinates inserted are not valid, try again");
                    i--;
                } else {
                    coordinates.add(columnWanted);
                    coordinates.add(rowWanted);
                }
            } catch (NumberFormatException e) {
                System.err.println("The number you inserted is not an integer, try again.");
                i--;
            }
        }
        return coordinates;
    }

    /**
     * Method used to print the tiles just taken
     * @param takenTiles the taken tiles
     */
    @Override
    public void printTakenTiles(List<ItemTile> takenTiles) {
        System.out.println("\nThese are the tiles that you took: ");
        for (int i = 0; i < takenTiles.size(); i++) {
            System.out.println(i + 1 + ": " + takenTiles.get(i).getCharacter());
        }
    }

    /**
     * Method used to ask in which order the tiles have to be placed
     * @param numberOfTilesWanted number of tiles wanted
     * @return the desired order
     */
    @Override
    public List<Integer> askOrderTile(int numberOfTilesWanted) {
        Scanner s = new Scanner(System.in);
        List<Integer> orderTiles = new ArrayList<>();
        int order = -1;

        System.out.println("Choose the order for placing the tiles.");
        for (int i = 0; i < numberOfTilesWanted; i++) {
            try {
                System.out.print("In which position do you want to place the tile n° ");
                System.out.print(i + 1);
                System.out.println(" (1 - " + numberOfTilesWanted + "): ");
                String orderString = s.next();
                order = Integer.parseInt(orderString);
                if (order <= 0 || order > numberOfTilesWanted) {
                    System.err.println("Not valid number inserted, try again");
                    i--;
                } else {
                    orderTiles.add(order - 1);
                }
            } catch (NumberFormatException e) {
                System.err.println("The number you inserted is not an integer, try again.");
                i--;
            }
        }
        return orderTiles;
    }

    /**
     * Method used to ask in which column the tiles should be placed
     * @param numberOfColumns number of columns in the bookshelf
     * @return the asked column
     */
    @Override
    public int askColumnPlaceTile(int numberOfColumns) {
        boolean correctChoice = false;
        int columnWanted = -1;
        Scanner s = new Scanner(System.in);

        while (correctChoice == false) {
            try {
                System.out.print("Choose the column where you want to place your tiles: (0 - "); //Asking the column where the player wants to add the tiles
                System.out.print(numberOfColumns - 1);
                System.out.println("): ");
                String columnWantedString = s.next();
                columnWanted = Integer.parseInt(columnWantedString);
                if (columnWanted >= 0 && columnWanted <= 4) {
                    correctChoice = true;
                } else {
                    System.err.println("Number inserted is not valid, try again:");
                }
            } catch (NumberFormatException e) {
                System.err.println("The number you inserted is not an integer, try again.");
            }
        }

        return columnWanted;
    }

    /**
     * Method to inform the achievement of a common goal
     * @param number common goal number
     */
    @Override
    public void completedCommonGoalCard(int number) {
        System.out.print("Congratulation! You completed the common goal n° ");
        System.out.println(number + 1);
    }

    /**
     * Method used to inform that the bookshelf is full and the game is ending
     */
    @Override
    public void playerHasFullBookshelf() {
        System.out.println("Congratulation! Your bookshelf is full. Game will end soon...");
    }

    /**
     * Prints a message, used for printPersonalBookshelf
     */
    @Override
    public void printThisIsYourBookShelf() {
        System.out.println("This is your bookshelf: ");
    }

    /**
     * Method that prints a single bookshelf, used for both printPersonalBookshelf and printAllBookshelf
     * @param personalBookshelf the bookshelf
     * @param numberOfRowsBookshelf number of rows of the bookshelf
     * @param numberOfColumnsBookshelf number of columns of the bookshelf
     */
    @Override
    public void printPersonalBookshelf(String[][] personalBookshelf, int numberOfRowsBookshelf, int numberOfColumnsBookshelf) {
        System.out.print("      ");
        for (int i = 0; i < numberOfColumnsBookshelf; i++) {
            System.out.print(i + "   ");
        }
        System.out.println();
        for (int i = 0; i < numberOfRowsBookshelf; i++) {
            System.out.println("    *---*---*---*---*---*");
            for (int j = 0; j < numberOfColumnsBookshelf; j++) {
                if (j == 0) {
                    System.out.print(i + "   | ");
                }
                System.out.print(personalBookshelf[i][j] + " | ");
            }
            System.out.println();
        }
        System.out.println("    *---*---*---*---*---*");
    }

    /**
     * Method that prints the scores for the game for each player
     * @param scores the scores of the players
     */
    @Override
    public void printScores(HashMap<String, Integer> scores) {
        System.out.println("This is the final scores for every player:");

        for (String nickname : scores.keySet()) {
            System.out.println(nickname + ": " + scores.get(nickname) + " points");
        }
        System.out.println();
    }

    /**
     * Method that prints the winner of the game
     * @param winner the winner
     */
    @Override
    public void printWinner(String winner) {
        System.out.println("The winner is " + winner + "!");
    }

    /**
     * Method used to inform that the game has ended
     */
    @Override
    public void printTheGameHasEnded() {
        System.out.println("\nThe game has ended\n");
    }

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
     * @param currentPBookshelf bookshelf of the player
     * @param newBook bookshelf of all players, on turn excluded
     */

    public void printAllInformation (String CurrentP, ItemTile[][] board, int numberOfRowsBoard, int numberOfColumnsBoard,
                                     int numberOfRowsBookshelf, int numberOfColumnsBookshelf,
                                     List<ItemTile[][]> AllPlayersBookshelf, List<String> commonGoalDescription,
                                     List<Integer> commonGoalPoints, List<String> nicknames, String[][] personalGoalCard,
                                     ItemTile[][] currentPBookshelf, List<ItemTile[][]> newBook) {

        System.out.println("\nA player has finished its turn. These are the board and bookshelf updated for player " + CurrentP + ":\n");
        System.out.print("      ");
        for (int i = 0; i < numberOfColumnsBoard; i++) {
            System.out.print(i + "   ");
        }
        System.out.println();

        for (int i = 0; i < numberOfRowsBoard; i++) {
            System.out.print("    *---*---*---*---*---*---*---*---*---*");
            if(i==0) System.out.print("         This is your personalGoalCard:");
            if(i==1) System.out.print("         *---*---*---*---*---*");
            if(i==2) System.out.print("         *---*---*---*---*---*");
            if(i==3) System.out.print("         *---*---*---*---*---*");
            if(i==4) System.out.print("         *---*---*---*---*---*");
            if(i==5) System.out.print("         *---*---*---*---*---*");
            if(i==6) System.out.print("         *---*---*---*---*---*");
            if(i==7) System.out.print("         *---*---*---*---*---*");
            System.out.println();
            for (int j = 0; j < numberOfColumnsBoard; j++) {
                if (j == 0) {
                    System.out.print(i + "   | ");
                }
                System.out.print(board[i][j].getCharacter() + " | ");
            }
                if(i==1){
                    System.out.print("     ");
                    for (int x = 0; x < numberOfColumnsBookshelf; x++) {
                        if (x == 0) {
                            System.out.print("   | ");
                        }
                        System.out.print(personalGoalCard[0][x] + " | ");
                    }
                }
                if(i==2){
                    System.out.print("     ");
                    for (int x = 0; x < numberOfColumnsBookshelf; x++) {
                        if (x == 0) {
                            System.out.print("   | ");
                        }
                        System.out.print(personalGoalCard[1][x] + " | ");
                    }
                }
                if(i==3){
                    System.out.print("     ");
                    for (int x = 0; x < numberOfColumnsBookshelf; x++) {
                        if (x == 0) {
                            System.out.print("   | ");
                        }
                        System.out.print(personalGoalCard[2][x] + " | ");
                    }
                }
                if(i==4){
                    System.out.print("     ");
                    for (int x = 0; x < numberOfColumnsBookshelf; x++) {
                        if (x == 0) {
                            System.out.print("   | ");
                        }
                        System.out.print(personalGoalCard[3][x] + " | ");
                    }
                }
                if(i==5){
                    System.out.print("     ");
                    for (int x = 0; x < numberOfColumnsBookshelf; x++) {
                        if (x == 0) {
                            System.out.print("   | ");
                        }
                        System.out.print(personalGoalCard[4][x] + " | ");
                    }
                }
                if(i==6){
                    System.out.print("     ");
                    for (int x = 0; x < numberOfColumnsBookshelf; x++) {
                        if (x == 0) {
                            System.out.print("   | ");
                        }
                        System.out.print(personalGoalCard[5][x] + " | ");
                    }
                }

            System.out.println();
        }
        System.out.println("    *---*---*---*---*---*---*---*---*---*");

        System.out.println();
        System.out.println("CommonGoal Description: "+commonGoalDescription.get(0));
        System.out.println("CommonGoalPoints: "+commonGoalPoints.get(0));
        System.out.println("CommonGoal Description: "+commonGoalDescription.get(1));
        System.out.println("CommonGoalPoints: " +commonGoalPoints.get(1));
        System.out.println();

        for (int i = 0; i < nicknames.size(); i++) {
            System.out.print("      ");
            System.out.print(nicknames.get(i));
            for(int j = nicknames.get(i).length(); j < 16; j++)
            {
                System.out.print(" ");
            }
            System.out.print("           ");
        }
        System.out.println();
        System.out.println();
        System.out.print("      ");

        for(int s=0;s< nicknames.size();s++) {

            for (int i = 0; i < numberOfColumnsBookshelf; i++) {
                System.out.print(i + "   ");
            }
            System.out.print("             ");
        }
        System.out.println();

        for (int i = 0; i < numberOfRowsBookshelf; i++) {

            for (int k = 0; k < nicknames.size(); k++) {
                System.out.print("    *---*---*---*---*---*        ");
            }
            System.out.println();
            for (int k = 0; k < nicknames.size(); k++) {
                for (int j = 0; j < numberOfColumnsBookshelf; j++) {
                    if (j == 0 /*&& k == 0*/) {
                        System.out.print(i);
                    }
                    if(/*k !=0 &&*/ j==0) System.out.print("   | ");
                    System.out.print(AllPlayersBookshelf.get(k)[i][j].getCharacter() + " | ");
                }
                System.out.print("       ");
            }
            System.out.println();
        }
        for (int k = 0; k < nicknames.size(); k++) {
            System.out.print("    *---*---*---*---*---*        ");
        }
    }

    /**
     * Method for printing that the server or a player has disconnected
     * @param type 0 for RMI client and server crash, 1 for RMI client and player crash, 2 for socket client
     */
    @Override
    public void printDisconnection(int type) {
        if(type == 0)
        {
            System.err.println("\nThe server has disconnected, ending the game...");
        }
        else if(type == 1)
        {
            System.err.println("\nA player has disconnected, ending the game...");
        }
        else
        {
            System.err.println("\nA player or the server has disconnected, ending the game...");
        }
        System.exit(0);
    }
}