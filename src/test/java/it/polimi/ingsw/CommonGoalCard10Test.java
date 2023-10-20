package it.polimi.ingsw;
import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;
public class CommonGoalCard10Test {
    /**
     * Testing the achievement of the goal number 10.
     */
    @Test
    void CommonGoalCard10Test() {
        Bookshelf commongoal10Excepted = new Bookshelf();
        commongoal10Excepted.getBookshelf()[0][0]= new ItemTile(ItemTileType.PLANT, 1);
        commongoal10Excepted.getBookshelf()[0][1]= new ItemTile(ItemTileType.PLANT, 1);
        commongoal10Excepted.getBookshelf()[0][2]= new ItemTile(ItemTileType.PLANT, 1);
        commongoal10Excepted.getBookshelf()[0][3]= new ItemTile(ItemTileType.PLANT, 1);
        commongoal10Excepted.getBookshelf()[0][4]= new ItemTile(ItemTileType.PLANT, 1);

        commongoal10Excepted.getBookshelf()[1][0]= new ItemTile(ItemTileType.PLANT, 1);
        commongoal10Excepted.getBookshelf()[1][1]= new ItemTile(ItemTileType.CAT, 1);
        commongoal10Excepted.getBookshelf()[1][2]= new ItemTile(ItemTileType.FRAME, 2);
        commongoal10Excepted.getBookshelf()[1][3]= new ItemTile(ItemTileType.BOOK, 3);
        commongoal10Excepted.getBookshelf()[1][4]= new ItemTile(ItemTileType.GAME, 1);

        commongoal10Excepted.getBookshelf()[2][0]= new ItemTile(ItemTileType.PLANT, 1);
        commongoal10Excepted.getBookshelf()[2][1]= new ItemTile(ItemTileType.CAT, 1);
        commongoal10Excepted.getBookshelf()[2][2]= new ItemTile(ItemTileType.EMPTY, 0);
        commongoal10Excepted.getBookshelf()[2][3]= new ItemTile(ItemTileType.BOOK, 1);
        commongoal10Excepted.getBookshelf()[2][4]= new ItemTile(ItemTileType.GAME, 1);



        CommonGoalCard10 trialcard = new CommonGoalCard10(2);
        boolean flag = trialcard.commonGoalAchieved(commongoal10Excepted);
        assertEquals(flag,false);

        commongoal10Excepted.getBookshelf()[3][0]= new ItemTile(ItemTileType.PLANT, 1);
        commongoal10Excepted.getBookshelf()[3][1]= new ItemTile(ItemTileType.CAT, 1);
        commongoal10Excepted.getBookshelf()[3][2]= new ItemTile(ItemTileType.FRAME, 1);
        commongoal10Excepted.getBookshelf()[3][3]= new ItemTile(ItemTileType.BOOK, 1);
        commongoal10Excepted.getBookshelf()[3][4]= new ItemTile(ItemTileType.GAME, 1);

        flag = trialcard.commonGoalAchieved(commongoal10Excepted);
        assertEquals(flag,true);

        commongoal10Excepted.getBookshelf()[4][0]= new ItemTile(ItemTileType.PLANT, 1);
        commongoal10Excepted.getBookshelf()[4][1]= new ItemTile(ItemTileType.CAT, 1);
        commongoal10Excepted.getBookshelf()[4][2]= new ItemTile(ItemTileType.FRAME, 1);
        commongoal10Excepted.getBookshelf()[4][3]= new ItemTile(ItemTileType.BOOK, 1);
        commongoal10Excepted.getBookshelf()[4][4]= new ItemTile(ItemTileType.GAME, 1);

        flag = trialcard.commonGoalAchieved(commongoal10Excepted);
        assertEquals(flag,true);

        Bookshelf bookshelf = new Bookshelf();
        bookshelf.getBookshelf()[4][0]= new ItemTile(ItemTileType.PLANT, 1);
        bookshelf.getBookshelf()[4][1]= new ItemTile(ItemTileType.CAT, 1);
        bookshelf.getBookshelf()[4][2]= new ItemTile(ItemTileType.FRAME, 1);

        bookshelf.getBookshelf()[5][0]= new ItemTile(ItemTileType.PLANT, 1);
        bookshelf.getBookshelf()[5][1]= new ItemTile(ItemTileType.CAT, 1);
        bookshelf.getBookshelf()[5][2]= new ItemTile(ItemTileType.FRAME, 1);

        assertEquals(false, trialcard.commonGoalAchieved(bookshelf));
    }
}
