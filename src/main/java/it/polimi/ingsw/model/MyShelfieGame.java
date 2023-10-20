package it.polimi.ingsw.model;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

/**
 * This class represents the game
 */
public class MyShelfieGame {
    private String gameName = " ";
    private List<Player> players;
    private Board board;
    private List<CommonGoalCard> commonGoalCards;
    private List<CommonGoalCard> commonGoalTypes;
    private List<Integer> personalGoalCardTypes;
    private boolean finalTokenTaken;
    private Player winner;

    /**
     * Constructor of MyShelfieGame, it initializes the list of common and personal goals, it creates a new board and
     * lists for players and current common goals
     * @param numOfPlayers number of players that will join the game
     * @throws IOException related to an error while reading or writing on a file
     * @throws URISyntaxException related to an error while reading or writing on a file
     */
    public MyShelfieGame(int numOfPlayers) throws URISyntaxException, IOException {
        players = new ArrayList<>();
        board = new Board(numOfPlayers);
        commonGoalCards = new ArrayList<>();
        commonGoalTypes = new ArrayList<>();
        personalGoalCardTypes = new ArrayList<>();

        //Adding all commonGoalCard
        CommonGoalCard card1 = new CommonGoalCard1_2(numOfPlayers, 2, 6);
        commonGoalTypes.add(card1);
        CommonGoalCard card2 = new CommonGoalCard1_2(numOfPlayers, 4, 4);
        commonGoalTypes.add(card2);
        CommonGoalCard card3 = new CommonGoalCard3(numOfPlayers);
        commonGoalTypes.add(card3);
        CommonGoalCard card4 = new CommonGoalCard4(numOfPlayers);
        commonGoalTypes.add(card4);
        CommonGoalCard card5 = new CommonGoalCard5(numOfPlayers);
        commonGoalTypes.add(card5);
        CommonGoalCard card6 = new CommonGoalCard6(numOfPlayers);
        commonGoalTypes.add(card6);
        CommonGoalCard card7 = new CommonGoalCard7(numOfPlayers);
        commonGoalTypes.add(card7);
        CommonGoalCard card8 = new CommonGoalCard8(numOfPlayers);
        commonGoalTypes.add(card8);
        CommonGoalCard card9 = new CommonGoalCard9(numOfPlayers);
        commonGoalTypes.add(card9);
        CommonGoalCard card10 = new CommonGoalCard10(numOfPlayers);
        commonGoalTypes.add(card10);
        CommonGoalCard card11 = new CommonGoalCard11(numOfPlayers);
        commonGoalTypes.add(card11);
        CommonGoalCard card12 = new CommonGoalCard12(numOfPlayers);
        commonGoalTypes.add(card12);

        //Counting how many PersonalGoalCard are in the file by counting how many tags PGC are found
        URI uri = ClassLoader.getSystemResource("PersonalGoalCardPool.txt").toURI();

        FileSystem fs = null;
        if ("jar".equals(uri.getScheme())) {
            for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
                if (provider.getScheme().equalsIgnoreCase("jar")) {
                    try {
                        fs = provider.getFileSystem(uri);
                    } catch (FileSystemNotFoundException e) {
                        fs = provider.newFileSystem(uri, Collections.emptyMap());
                    }
                }
            }
        }

        String tag = "PGC";
        int numberOfPersonalGoalCard = 0;

        List<String> fileRows = Files.lines(Paths.get(uri)).toList();
        Scanner s = new Scanner(fileRows.get(0));
        int row = 0;

        while (row != fileRows.size()) {
            s = new Scanner(fileRows.get(row));
            String fileScanner = s.next();
            if (fileScanner.contains(tag)) {
                numberOfPersonalGoalCard++;
                personalGoalCardTypes.add(numberOfPersonalGoalCard);
            }
            row++;
        }
        s.close();
        if (fs != null) {
            fs.close();
        }
    }

    /**
     * Method called in the controller when all players have joined.
     * It chooses two random common Goals, assign to every player a different personal Goal, chooses the order and refill the board for the first time
     * @throws IOException related to an error while reading or writing on a file
     * @throws URISyntaxException related to an error while reading or writing on a file
     */
    public void start() throws IOException, URISyntaxException {
        //Choosing the commonGoals
        int numberOfCommonGoals = 2;
        for (int i = 0; i < numberOfCommonGoals; i++) {
            addCommonGoal();
        }

        //Saving numberOfPlayers and nicknames on file
        String fileNameNicknames = System.getProperty("user.dir") + "/Persistence/" + gameName + "Game.txt";
        FileWriter fileWriter = new FileWriter(fileNameNicknames, true);
        fileWriter.write(players.size() + "\n");
        for (Player player : players) {
            fileWriter.write(player.getNickname() + "\n");
        }
        fileWriter.close();

        //Choosing for every player a random PersonalGoalCard
        String fileNamePersonal = System.getProperty("user.dir") + "/Persistence/" + gameName + "PersonalGoalCard.txt";

        File file = new File(fileNamePersonal);
        if (file.exists() == false) {
            file.createNewFile();
        }

        fileWriter = new FileWriter(fileNamePersonal);
        Random rnd = new Random();
        for (Player player : players) {
            int chosenPersonalGoalCardIndex = rnd.nextInt(personalGoalCardTypes.size());
            int chosenPersonalGoalCard = personalGoalCardTypes.remove(chosenPersonalGoalCardIndex);
            PersonalGoalCard personalGoalCard = new PersonalGoalCard(chosenPersonalGoalCard);
            fileWriter.write(chosenPersonalGoalCard + "\n");
            player.setPersonalGoalCard(personalGoalCard);
        }

        fileWriter.close();

        //Adding first tiles to the board
        board.refill(gameName);

        board.writeOnFile(gameName);

        finalTokenTaken = false;
    }

    /**
     * Method for adding a new random Common Goal card to the list commonGoalCards
     */
    private void addCommonGoal() throws IOException {
        //Creating file for saving that a player has finished the game
        String fileNameCommonGoal = System.getProperty("user.dir") + "/Persistence/" + gameName + "CommonGoalCard.txt";

        File file = new File(fileNameCommonGoal);
        if (file.exists() == false) {
            file.createNewFile();
        }

        Random rnd = new Random();
        int n = rnd.nextInt(commonGoalTypes.size());
        CommonGoalCard card = commonGoalTypes.get(n);
        commonGoalCards.add(card);
        commonGoalTypes.remove(card);

        FileWriter fileWriter = new FileWriter(fileNameCommonGoal, true);
        fileWriter.write(n + "\n");
        fileWriter.close();
    }

    /**
     * It stores the common goal being used in the game
     * @param ID of the common goal
     */
    public void addCommonGoalPersistence(int ID)
    {
        CommonGoalCard card = commonGoalTypes.get(ID);
        commonGoalCards.add(card);
        commonGoalTypes.remove(card);
    }

    /**
     * Writing on file the board
     * @throws IOException related to an error while reading or writing on a file
     */
    public void writeBoardOnFile() throws IOException {
        board.writeOnFile(gameName);
    }

    /**
     * Adding a player to the list
     * @param p player
     */
    public void addPlayer(Player p)
    {
        players.add(p);
    }

    /**
     * Getter method for list players
     * @return the list players
     */
    public List<Player> getPlayers(){return players;}

    /**
     * Getter method for the board
     * @return the board
     */
    public Board getGameBoard(){return board;}

    /**
     * Getter for commonGoalCards
     * @return the list commonGoalCards
     */
    public List<CommonGoalCard> getCommonGoals(){
        return commonGoalCards;
    }

    /**
     * Getter method of finalTokenTaken
     * @return boolean finalTokenTaken
     */
    public boolean isFinalTokenTaken() {
        return finalTokenTaken;
    }

    /**
     * Setter method of finalTokenTaken
     * @param finalTokenTaken true if the final token has been taken, false otherwise
     */
    public void setFinalTokenTaken(boolean finalTokenTaken) {
        this.finalTokenTaken = finalTokenTaken;
    }

    /**
     * Getter method of winner
     * @return the winner
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Setter method of winner
     * @param winner the winner player
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Getter method for parameter gameName
     * @return the game's name
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Setter method for parameter gameName
     * @param gameName the name's game
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
