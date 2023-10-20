package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.RowAndColumnNotFoundException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

public class CommonGoalCard5Test {
    /**
     * Testing the achievement of the goal of the card number 5
     */
    @Test
    void CommonGoalCard5Test() throws RowAndColumnNotFoundException {
        Bookshelf bookshelf = new Bookshelf();
        CommonGoalCard5 cardTest = new CommonGoalCard5(3);

        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);

        assertEquals(false, cardTest.commonGoalAchieved(bookshelf));

        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 1);

        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 3) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 2);

        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);

        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 4);

        assertEquals(true, cardTest.commonGoalAchieved(bookshelf));
    }
}
