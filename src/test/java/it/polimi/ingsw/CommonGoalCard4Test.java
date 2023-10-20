package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.RowAndColumnNotFoundException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

public class CommonGoalCard4Test {
    /**
     * Testing the achievement of the goal of the card number 4
     */
    @Test
    void CommonGoalCard4Test() throws RowAndColumnNotFoundException {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 3) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1), 0);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 0);

        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 3) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 1);

        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);

        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 3);

        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 2) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 4);

        CommonGoalCard4 cardTest = new CommonGoalCard4(3);

        assertEquals(true, cardTest.commonGoalAchieved(bookshelf));
    }

    @Test
    public void everyPossibileCombinationTest()
    {
        Bookshelf bookshelf = new Bookshelf();
        CommonGoalCard4 cardTest = new CommonGoalCard4(2);

        bookshelf.getBookshelf()[4][3] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf.getBookshelf()[5][3] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf.getBookshelf()[4][4] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf.getBookshelf()[5][4] = new ItemTile(ItemTileType.CAT, 1);

        for(int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if(i != 2 || j <=1) {
                    bookshelf.getBookshelf()[i][j] = new ItemTile(ItemTileType.CAT, 1);
                    bookshelf.getBookshelf()[i + 1][j] = new ItemTile(ItemTileType.CAT, 1);
                    bookshelf.getBookshelf()[i][j + 1] = new ItemTile(ItemTileType.CAT, 1);
                    bookshelf.getBookshelf()[i + 1][j + 1] = new ItemTile(ItemTileType.CAT, 1);
                    assertEquals(true, cardTest.commonGoalAchieved(bookshelf));
                    bookshelf.getBookshelf()[i][j] = new ItemTile(ItemTileType.EMPTY, 1);
                    bookshelf.getBookshelf()[i + 1][j] = new ItemTile(ItemTileType.EMPTY, 1);
                    bookshelf.getBookshelf()[i][j + 1] = new ItemTile(ItemTileType.EMPTY, 1);
                    bookshelf.getBookshelf()[i + 1][j + 1] = new ItemTile(ItemTileType.EMPTY, 1);
                }
            }
        }

        bookshelf.getBookshelf()[4][3] = new ItemTile(ItemTileType.EMPTY, 1);
        bookshelf.getBookshelf()[5][3] = new ItemTile(ItemTileType.EMPTY, 1);
        bookshelf.getBookshelf()[4][4] = new ItemTile(ItemTileType.EMPTY, 1);
        bookshelf.getBookshelf()[5][4] = new ItemTile(ItemTileType.EMPTY, 1);
        bookshelf.getBookshelf()[0][0] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf.getBookshelf()[0][1] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf.getBookshelf()[1][0] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf.getBookshelf()[1][1] = new ItemTile(ItemTileType.CAT, 1);

        for(int i = 2; i < 5; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if(i != 2 || j >=2) {
                    bookshelf.getBookshelf()[i][j] = new ItemTile(ItemTileType.CAT, 1);
                    bookshelf.getBookshelf()[i + 1][j] = new ItemTile(ItemTileType.CAT, 1);
                    bookshelf.getBookshelf()[i][j + 1] = new ItemTile(ItemTileType.CAT, 1);
                    bookshelf.getBookshelf()[i + 1][j + 1] = new ItemTile(ItemTileType.CAT, 1);
                    assertEquals(true, cardTest.commonGoalAchieved(bookshelf));
                    bookshelf.getBookshelf()[i][j] = new ItemTile(ItemTileType.EMPTY, 1);
                    bookshelf.getBookshelf()[i + 1][j] = new ItemTile(ItemTileType.EMPTY, 1);
                    bookshelf.getBookshelf()[i][j + 1] = new ItemTile(ItemTileType.EMPTY, 1);
                    bookshelf.getBookshelf()[i + 1][j + 1] = new ItemTile(ItemTileType.EMPTY, 1);
                }
            }
        }
    }
}
