package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.RowAndColumnNotFoundException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

public class PersonalGoalCardTest {

    /**
     * First test for PersonalGoalCardClass, testing PersonalGoalCard1
     */
    @Test
    void personalGoalTest() throws RowAndColumnNotFoundException, URISyntaxException, IOException {
        PersonalGoalCard personalGoalCardTestExcepted = new PersonalGoalCard(1);
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                personalGoalCardTestExcepted.getPersonalGoalCard()[i][j] = new ItemTile(ItemTileType.EMPTY, 0);
            }
        }
        personalGoalCardTestExcepted.getPersonalGoalCard()[0][0] = new ItemTile(ItemTileType.PLANT, 1);
        personalGoalCardTestExcepted.getPersonalGoalCard()[0][2] = new ItemTile(ItemTileType.FRAME, 1);
        personalGoalCardTestExcepted.getPersonalGoalCard()[1][4] = new ItemTile(ItemTileType.CAT, 1);
        personalGoalCardTestExcepted.getPersonalGoalCard()[2][3] = new ItemTile(ItemTileType.BOOK, 1);
        personalGoalCardTestExcepted.getPersonalGoalCard()[3][1] = new ItemTile(ItemTileType.GAME, 1);
        personalGoalCardTestExcepted.getPersonalGoalCard()[5][2] = new ItemTile(ItemTileType.TROPHIE, 1);

        PersonalGoalCard personalGoalCardTestActual = new PersonalGoalCard(1);

        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                assertEquals(personalGoalCardTestExcepted.getPersonalGoalCard()[i][j].getItemTileType(), personalGoalCardTestActual.getPersonalGoalCard()[i][j].getItemTileType());
            }
        }

        Bookshelf bookshelfTestActual = new Bookshelf();
        for(int i = 0; i < 6; i++)
        {
            bookshelfTestActual.addTile(new ItemTile(ItemTileType.PLANT, 1), 0);
            bookshelfTestActual.addTile(new ItemTile(ItemTileType.GAME, 1), 1);
            bookshelfTestActual.addTile(new ItemTile(ItemTileType.BOOK, 1), 3);
            bookshelfTestActual.addTile(new ItemTile(ItemTileType.CAT, 1), 4);
        }

        bookshelfTestActual.addTile(new ItemTile(ItemTileType.CAT, 1), 2);
        bookshelfTestActual.addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        bookshelfTestActual.addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        bookshelfTestActual.addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        bookshelfTestActual.addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        bookshelfTestActual.addTile(new ItemTile(ItemTileType.FRAME, 1), 2);

        int pointsActual = personalGoalCardTestActual.calculatePersonalGoalPoints(bookshelfTestActual);
        assertEquals(9, pointsActual);
    }


    /**
     * Testing, for all PersonalGoalCard in PersonalGoalCardPool.txt, if calculate points method is working in every case (0, 1, 2, 4, 6, 9, 12)
     */
    @Test
    void allPersonalGoalCard() throws RowAndColumnNotFoundException, URISyntaxException, IOException {
        for(int i = 1; i <= 12; i++)
        {
            ArrayList<Integer> row = new ArrayList<Integer>();
            ArrayList<Integer> columns = new ArrayList<Integer>();
            ArrayList<Integer> itemType = new ArrayList<Integer>();

            PersonalGoalCard personalGoalCardActual = new PersonalGoalCard(i);

            String personalGoalSection = "PGC" + i;
            String fileName = System.getProperty("user.dir") + "/resources/PersonalGoalCardPool.txt";

            try (Scanner s = new Scanner(new FileReader(fileName)))
            {
                while (s.hasNext())
                {
                    String personalGoalSectionPool = s.next();
                    if(personalGoalSectionPool.equals(personalGoalSection))
                    {
                        for(int k = 0; k < 6; k++)
                        {
                            row.add(s.nextInt());
                            columns.add(s.nextInt());
                            itemType.add(s.nextInt());
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("The file cannot be found");
            }

            for(int j = 0; j < 7; j++)
            {
                int[] remainingTiles = new int[6];
                for(int s = 0; s < 6; s++)
                {
                    remainingTiles[s] = 6;
                }

                Bookshelf bookshelfToTest = new Bookshelf();
                for(int s = 0; s < j; s++)
                {
                    int rowItem = row.get(5 - s);
                    int columnItem = columns.get(5 - s);
                    int itemTypeItem = itemType.get(5 - s);

                    while(remainingTiles[columnItem] - 1 > rowItem)
                    {
                        bookshelfToTest.addTile(new ItemTile(ItemTileType.CAT, 1), columnItem);
                        remainingTiles[columnItem]--;
                    }

                    switch (itemTypeItem)
                    {
                        case 1:
                            bookshelfToTest.addTile(new ItemTile(ItemTileType.CAT, 1), columnItem);
                            break;
                        case 2:
                            bookshelfToTest.addTile(new ItemTile(ItemTileType.BOOK, 1), columnItem);
                            break;
                        case 3:
                            bookshelfToTest.addTile(new ItemTile(ItemTileType.GAME, 1), columnItem);
                            break;
                        case 4:
                            bookshelfToTest.addTile(new ItemTile(ItemTileType.FRAME, 1), columnItem);
                            break;
                        case 5:
                            bookshelfToTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), columnItem);
                            break;
                        case 6:
                            bookshelfToTest.addTile(new ItemTile(ItemTileType.PLANT, 1), columnItem);
                            break;
                    }
                    remainingTiles[columnItem]--;
                }

                boolean toChange = false;
                int itemToChange = 0;

                for(int s = 0; s < 5; s++)
                {
                    while (remainingTiles[s] > 0)
                    {
                        for(int t = 0; t < 6; t++)
                        {
                            if(s == columns.get(t) && (remainingTiles[s] - 1) == row.get(t))
                            {
                                toChange = true;
                                itemToChange = itemType.get(t);
                            }
                        }
                        if(toChange == true)
                        {
                            if(itemToChange == 1)
                            {
                                bookshelfToTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), s);
                            }
                            else
                            {
                                bookshelfToTest.addTile(new ItemTile(ItemTileType.CAT, 1), s);
                            }
                            toChange = false;
                        }
                        else
                        {
                            bookshelfToTest.addTile(new ItemTile(ItemTileType.CAT, 1), s);
                        }
                        remainingTiles[s]--;
                    }
                }

                switch (j)
                {
                    case 0:
                        assertEquals(0, personalGoalCardActual.calculatePersonalGoalPoints(bookshelfToTest));
                        break;
                    case 1:
                        assertEquals(1, personalGoalCardActual.calculatePersonalGoalPoints(bookshelfToTest));
                        break;
                    case 2:
                        assertEquals(2, personalGoalCardActual.calculatePersonalGoalPoints(bookshelfToTest));
                        break;
                    case 3:
                        assertEquals(4, personalGoalCardActual.calculatePersonalGoalPoints(bookshelfToTest));
                        break;
                    case 4:
                        assertEquals(6, personalGoalCardActual.calculatePersonalGoalPoints(bookshelfToTest));
                        break;
                    case 5:
                        assertEquals(9, personalGoalCardActual.calculatePersonalGoalPoints(bookshelfToTest));
                        break;
                    case 6:
                        assertEquals(12, personalGoalCardActual.calculatePersonalGoalPoints(bookshelfToTest));
                        break;
                }
            }
        }
    }
}