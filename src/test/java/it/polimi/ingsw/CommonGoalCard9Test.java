package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.RowAndColumnNotFoundException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

public class CommonGoalCard9Test {
    /**
     * Testing the achievement of the goal of the card number 9
     */
    @Test
    void CommonGoalCard9Test() throws RowAndColumnNotFoundException {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 0);

        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);

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
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 2) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 2) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 3) , 4);

        CommonGoalCard9 cardTest = new CommonGoalCard9(3);

        assertEquals(false, cardTest.commonGoalAchieved(bookshelf));

        Bookshelf bookshelf1 = new Bookshelf();
        bookshelf1.addTile(new ItemTile(ItemTileType.BOOK, 1) , 0);
        bookshelf1.addTile(new ItemTile(ItemTileType.CAT, 1) , 0);
        bookshelf1.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 0);
        bookshelf1.addTile(new ItemTile(ItemTileType.GAME, 1) , 0);
        bookshelf1.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);
        bookshelf1.addTile(new ItemTile(ItemTileType.FRAME, 1) , 0);

        bookshelf1.addTile(new ItemTile(ItemTileType.BOOK, 1) , 2);
        bookshelf1.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);
        bookshelf1.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 2);
        bookshelf1.addTile(new ItemTile(ItemTileType.GAME, 1) , 2);
        bookshelf1.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);
        bookshelf1.addTile(new ItemTile(ItemTileType.FRAME, 1) , 2);

        assertEquals(true, cardTest.commonGoalAchieved(bookshelf1));
    }
}
