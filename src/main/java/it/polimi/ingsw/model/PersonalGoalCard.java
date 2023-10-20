package it.polimi.ingsw.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents the PersonalGoalCard, each player has one, and the goal is to have as many tiles as possible
 * on the personal bookshelf of the same type and color represented on the card.
 */
public class PersonalGoalCard {
    private ItemTile[][] personalGoalCard;

    /**
     * Constructor of PersonalGoalCard class. It reads from a file called PersonalGoalCardPool.txt
     * @param personalGoalCardNumber indicates which type of personal goal card to take from PersonalGoalCardPool.txt
     * @throws IOException related to an error while reading or writing on a file
     * @throws URISyntaxException related to an error while reading or writing on a file
     */
    public PersonalGoalCard(int personalGoalCardNumber) throws URISyntaxException, IOException {
        int r = 6;
        int c = 5;
        int numberOfPersonalGoals = 6;

        //Every pair of coordinates are preceded from a tag, PGC + a number from 1 to 12
        String personalGoalSection = "PGC" + personalGoalCardNumber;
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

        //Initializing the matrix as ItemTile.EMPTY
        personalGoalCard = new ItemTile[6][5];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                personalGoalCard[i][j] = new ItemTile(ItemTileType.EMPTY, 0);
            }
        }

        List<String> fileRows = Files.lines(Paths.get(uri)).toList();
        Scanner s = new Scanner(fileRows.get(0));
        int row = 0;
        while (row != fileRows.size()) {
            //Reading from the txt file coordinates and tag
            s = new Scanner(fileRows.get(row));
            String personalGoalSectionPool = s.next();
            if (personalGoalSectionPool.equals(personalGoalSection)) {
                row++;
                for (int i = 0; i < numberOfPersonalGoals; i++) {
                    s = new Scanner(fileRows.get(row));
                    int firstCoordinate = s.nextInt();
                    int secondCoordinate = s.nextInt();
                    int tileType = s.nextInt();

                    //Every type is identified by an integer
                    switch (tileType) {
                        case 1:
                            personalGoalCard[firstCoordinate][secondCoordinate] = new ItemTile(ItemTileType.CAT, 1);
                            break;
                        case 2:
                            personalGoalCard[firstCoordinate][secondCoordinate] = new ItemTile(ItemTileType.BOOK, 1);
                            ;
                            break;
                        case 3:
                            personalGoalCard[firstCoordinate][secondCoordinate] = new ItemTile(ItemTileType.GAME, 1);
                            ;
                            break;
                        case 4:
                            personalGoalCard[firstCoordinate][secondCoordinate] = new ItemTile(ItemTileType.FRAME, 1);
                            ;
                            break;
                        case 5:
                            personalGoalCard[firstCoordinate][secondCoordinate] = new ItemTile(ItemTileType.TROPHIE, 1);
                            ;
                            break;
                        case 6:
                            personalGoalCard[firstCoordinate][secondCoordinate] = new ItemTile(ItemTileType.PLANT, 1);
                            break;
                    }
                    row++;
                }
            } else {
                row++;
            }
        }
        s.close();
        if (fs != null) {
            fs.close();
        }
    }


    /**
     * Method for calculating points, given a bookshelf
     * @param bookshelf bookshelf to analyze
     * @return points scored
     */
    public int calculatePersonalGoalPoints(Bookshelf bookshelf)
    {
        int r = 6;
        int c = 5;

        int numberCorrectPosition = 0;
        int finalPoints = 0;
        for(int i = 0; i < r; i++) { //Counting the correct position of tiles
            for (int j = 0; j < c; j++) {
                if (personalGoalCard[i][j].getItemTileType() != ItemTileType.EMPTY) {
                    if (personalGoalCard[i][j].getItemTileType() == bookshelf.getBookshelf()[i][j].getItemTileType()) {
                        numberCorrectPosition++;
                    }
                }
            }
        }

        //Returning points scored
        switch(numberCorrectPosition)
        {
            case 0:
                finalPoints = 0;
                break;
            case 1:
                finalPoints = 1;
                break;
            case 2:
                finalPoints = 2;
                break;
            case 3:
                finalPoints = 4;
                break;
            case 4:
                finalPoints = 6;
                break;
            case 5:
                finalPoints = 9;
                break;
            case 6:
                finalPoints = 12;
                break;
        }
        return finalPoints;
    }

    /**
     * Getter of the personalGoalCard
     * @return attribute personalGoalCard
     */

    public ItemTile[][] getPersonalGoalCard(){return personalGoalCard;}

}