package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.RowAndColumnNotFoundException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

public class BookshelfTest {

    /**
     * Test for method BookshelfAddTile()
     */
    @Test
    void BookshelfAddTileTest() throws RowAndColumnNotFoundException {
        Bookshelf bookshelfTestActual = new Bookshelf();


        bookshelfTestActual.addTile(new ItemTile(ItemTileType.BOOK, 1), 0);
        bookshelfTestActual.addTile(new ItemTile(ItemTileType.FRAME, 1), 0);

        assertEquals(ItemTileType.BOOK, bookshelfTestActual.getBookshelf()[5][0].getItemTileType());
        assertEquals(1, bookshelfTestActual.getBookshelf()[5][0].getType());
        assertEquals(ItemTileType.FRAME, bookshelfTestActual.getBookshelf()[4][0].getItemTileType());
        assertEquals(1, bookshelfTestActual.getBookshelf()[4][0].getType());
    }

    /**
     * Test for method BookshelfIsFull()
     */
    @Test
    void BookshelfIsFullTest() throws RowAndColumnNotFoundException {
        Bookshelf bookshelfActual = new Bookshelf();

        assertFalse(bookshelfActual.isFull());

        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 6; j++)
            {
                bookshelfActual.addTile(new ItemTile(ItemTileType.TROPHIE, 1), i);
            }
        }

        assertFalse(bookshelfActual.isFull());

        for(int i = 0; i < 6; i++)
        {
            bookshelfActual.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 4);
        }

        assertTrue(bookshelfActual.isFull());
    }


    /**
     * Test to check what happens if a user try to add a tile in a full column
     * @throws RowAndColumnNotFoundException if a tile is added in a full column
     */
    @Test
    void AddFullColumnTest() throws RowAndColumnNotFoundException
    {
        boolean usedcatch=false;
        Bookshelf bookshelfActual = new Bookshelf();
        for(int i = 0; i < 6; i++)
        {
            bookshelfActual.addTile(new ItemTile(ItemTileType.CAT, 1), 0);
        }
        try
        {
            bookshelfActual.addTile(new ItemTile(ItemTileType.CAT, 1), 0);
        }
        catch (RowAndColumnNotFoundException e)
        {
            usedcatch=true;
        }
        assert usedcatch;
    }

    /**
     * Test for method calculateScoreAdjacentItemTiles, case bookshelf full of the same type
     * @throws RowAndColumnNotFoundException if a tile is added in a full column
     */
    @Test
    void BookshelfAllSameItemsAdjacentPoints() throws RowAndColumnNotFoundException {
        Bookshelf bookshelfTest = new Bookshelf();
        for(int i = 0; i < 5; i++)
        {
            for(int j = 0; j < 6; j++)
            {
                bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), i);
            }
        }

        int totalPointsTest = bookshelfTest.calculateScoreAdjacentItemTiles();
        assertEquals(8,totalPointsTest);
    }

    /**
     * Test for method calculateScoreAdjacentItemTiles, case bookshelf full of different type
     * @throws RowAndColumnNotFoundException if a tile is added in a full column
     */
    @Test
    void BookshelfAllDifferentItemsAdjacentPoints() throws RowAndColumnNotFoundException
    {
        Bookshelf bookshelfTest = new Bookshelf();

        assertEquals(0, bookshelfTest.calculateScoreAdjacentItemTiles());

        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.GAME, 1), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.FRAME, 1), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.BOOK, 1), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 1), 0);

        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.FRAME, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.BOOK, 1), 1);

        bookshelfTest.addTile(new ItemTile(ItemTileType.BOOK, 1), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 1), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.GAME, 1), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.FRAME, 1), 2);

        bookshelfTest.addTile(new ItemTile(ItemTileType.FRAME, 1), 3);
        bookshelfTest.addTile(new ItemTile(ItemTileType.BOOK, 1), 3);
        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 1), 3);
        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 3);
        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        bookshelfTest.addTile(new ItemTile(ItemTileType.GAME, 1), 3);

        bookshelfTest.addTile(new ItemTile(ItemTileType.GAME, 1), 4);
        bookshelfTest.addTile(new ItemTile(ItemTileType.FRAME, 1), 4);
        bookshelfTest.addTile(new ItemTile(ItemTileType.BOOK, 1), 4);
        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 1), 4);
        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 4);
        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), 4);

        assertEquals(0, bookshelfTest.calculateScoreAdjacentItemTiles());
    }

    /**
     * Test for method calculateScoreAdjacentItemTiles, with some examples
     * @throws RowAndColumnNotFoundException if a tile is added in a full column
     */
    @Test
    void BookshelfAdjacentPointsTest() throws RowAndColumnNotFoundException {
        Bookshelf bookshelfTest = new Bookshelf();

        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 3), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.FRAME, 2), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.BOOK, 2), 0);
        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 1), 0);

        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.BOOK, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 1), 1);
        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 1), 1);

        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 2), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 2), 2);
        bookshelfTest.addTile(new ItemTile(ItemTileType.PLANT, 1), 2);

        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        bookshelfTest.addTile(new ItemTile(ItemTileType.GAME, 3), 3);
        bookshelfTest.addTile(new ItemTile(ItemTileType.BOOK, 3), 3);
        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 3), 3);

        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), 4);
        bookshelfTest.addTile(new ItemTile(ItemTileType.CAT, 1), 4);

        int totalPointsTest = bookshelfTest.calculateScoreAdjacentItemTiles();
        assertEquals(18, totalPointsTest);

        Bookshelf bookshelfTest2 = new Bookshelf();
        bookshelfTest2.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.FRAME, 1), 0);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.BOOK, 1), 0);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.PLANT, 1), 0);

        bookshelfTest2.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.BOOK, 1), 1);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.PLANT, 1), 1);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.PLANT, 1), 1);

        bookshelfTest2.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.CAT, 1), 2);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.PLANT, 1), 2);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.GAME, 1), 2);

        bookshelfTest2.addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.GAME, 1), 3);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.BOOK, 1), 3);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.CAT, 1), 3);

        bookshelfTest2.addTile(new ItemTile(ItemTileType.CAT, 1), 4);
        bookshelfTest2.addTile(new ItemTile(ItemTileType.CAT, 1), 4);

        int totalPointsTest2 = bookshelfTest2.calculateScoreAdjacentItemTiles();
        assertEquals(16, totalPointsTest2);

        Bookshelf bookshelfTest3 = new Bookshelf();

        bookshelfTest3.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.FRAME, 1), 0);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.PLANT, 1), 0);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.PLANT, 1), 0);

        bookshelfTest3.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.BOOK, 1), 1);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.PLANT, 1), 1);

        bookshelfTest3.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.CAT, 1), 2);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.GAME, 1), 2);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.GAME, 1), 2);

        bookshelfTest3.addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.GAME, 1), 3);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.BOOK, 1), 3);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.CAT, 1), 3);

        bookshelfTest3.addTile(new ItemTile(ItemTileType.CAT, 1), 4);
        bookshelfTest3.addTile(new ItemTile(ItemTileType.CAT, 1), 4);

        int totalPointsTest3 = bookshelfTest3.calculateScoreAdjacentItemTiles();
        assertEquals(17, totalPointsTest3);

        assertEquals(4, bookshelfTest3.numberOfPlaceableTiles());
    }

    @Test
    public void CalculateNumberGroupsTest() throws RowAndColumnNotFoundException {
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1), 0);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1), 0);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1), 0);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 2), 1);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 2), 1);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 2), 1);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 3), 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 3), 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 3), 2);
        assertEquals(1, bookshelf.calculateNumberGroups(9));
    }
}
