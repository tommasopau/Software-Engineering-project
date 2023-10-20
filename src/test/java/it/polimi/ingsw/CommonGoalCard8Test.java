package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.RowAndColumnNotFoundException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

public class CommonGoalCard8Test {
    @Test

    void CommonGoalCard8Test() throws RowAndColumnNotFoundException {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 0);

        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1), 1);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);

        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 2);

        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);

        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 4);

        CommonGoalCard8 cardTest = new CommonGoalCard8(3);

        assertEquals(true, cardTest.commonGoalAchieved(bookshelf));
    }

    @Test
    public void commonGoal8NotCompletedTest() throws RowAndColumnNotFoundException {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1), 1);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 1);

        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 2);

        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.PLANT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 3);

        CommonGoalCard8 cardTest = new CommonGoalCard8(2);

        assertEquals(false, cardTest.commonGoalAchieved(bookshelf));
    }
}

