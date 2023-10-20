package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.RowAndColumnNotFoundException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

public class CommonGoalCard7Test {
    /**
     * Testing the achievement of the goal of the card number 7
     */
    @Test
    void CommonGoalCard7Test() throws RowAndColumnNotFoundException {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 3) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 2) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.EMPTY, 0) , 0);

        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 2) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);

        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);

        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 3) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 3);

        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 4);

        CommonGoalCard7 cardTest = new CommonGoalCard7(3);

        assertEquals(false, cardTest.commonGoalAchieved(bookshelf));

        Bookshelf bookshelf1 = new Bookshelf();
        bookshelf1.getBookshelf()[5][0] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf1.getBookshelf()[4][1] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf1.getBookshelf()[3][2] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf1.getBookshelf()[2][3] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf1.getBookshelf()[1][4] = new ItemTile(ItemTileType.CAT, 1);

        assertEquals(true, cardTest.commonGoalAchieved(bookshelf1));

        Bookshelf bookshelf2 = new Bookshelf();
        bookshelf2.getBookshelf()[0][0] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf2.getBookshelf()[1][0] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[1][1] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf2.getBookshelf()[2][0] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[2][1] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[2][2] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf2.getBookshelf()[3][0] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[3][1] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[3][2] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[3][3] = new ItemTile(ItemTileType.CAT, 1);
        bookshelf2.getBookshelf()[4][0] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[4][1] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[4][2] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[4][3] = new ItemTile(ItemTileType.GAME, 1);
        bookshelf2.getBookshelf()[4][4] = new ItemTile(ItemTileType.CAT, 2);

        assertEquals(true, cardTest.commonGoalAchieved(bookshelf2));
    }
}
