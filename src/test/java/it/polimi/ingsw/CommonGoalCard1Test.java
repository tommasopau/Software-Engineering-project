package it.polimi.ingsw;
import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;
public class CommonGoalCard1Test {
    /**
     *Testing the achievement of the goal number 1.
     */
    @Test
    void CommonGoalCard1Test() {
        Bookshelf commongoal1Excepted = new Bookshelf();
        CommonGoalCard trialcard = new CommonGoalCard1_2(2, 2, 6);


        commongoal1Excepted.getBookshelf()[0][0]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal1Excepted.getBookshelf()[1][0]=new ItemTile(ItemTileType.CAT, 1);
        commongoal1Excepted.getBookshelf()[3][4]=new ItemTile(ItemTileType.CAT, 1);
        commongoal1Excepted.getBookshelf()[4][4]=new ItemTile(ItemTileType.CAT, 1);

        commongoal1Excepted.getBookshelf()[0][1]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[0][2]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[2][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[2][1]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[3][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[3][1]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[4][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[5][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[4][2]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[4][3]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[5][2]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[5][3]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[5][4]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[1][1]=new ItemTile(ItemTileType.TROPHIE, 1);
        commongoal1Excepted.getBookshelf()[1][2]=new ItemTile(ItemTileType.TROPHIE, 1);
        commongoal1Excepted.getBookshelf()[2][2]=new ItemTile(ItemTileType.TROPHIE, 1);

        commongoal1Excepted.getBookshelf()[0][3]=new ItemTile(ItemTileType.FRAME, 1);
        commongoal1Excepted.getBookshelf()[0][4]=new ItemTile(ItemTileType.FRAME, 1);
        commongoal1Excepted.getBookshelf()[1][4]=new ItemTile(ItemTileType.FRAME, 1);
        commongoal1Excepted.getBookshelf()[2][4]=new ItemTile(ItemTileType.FRAME, 1);

        commongoal1Excepted.getBookshelf()[3][2]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal1Excepted.getBookshelf()[3][3]=new ItemTile(ItemTileType.PLANT, 1);

        commongoal1Excepted.getBookshelf()[1][3]=new ItemTile(ItemTileType.BOOK, 1);
        commongoal1Excepted.getBookshelf()[2][3]=new ItemTile(ItemTileType.BOOK, 1);
        commongoal1Excepted.getBookshelf()[4][1]=new ItemTile(ItemTileType.BOOK, 1);
        commongoal1Excepted.getBookshelf()[5][1]=new ItemTile(ItemTileType.BOOK, 1);
        boolean flag = trialcard.commonGoalAchieved(commongoal1Excepted);
        assertEquals(flag, true);

        String description = trialcard.getDescription();
        assertEquals(8, trialcard.getFirstTokenAvailable());
        assertEquals(8, trialcard.getOnTop());
        assertEquals(4, trialcard.getFirstTokenAvailable());
        assertEquals(4, trialcard.getOnTop());
        assertEquals(0, trialcard.getFirstTokenAvailable());
        assertEquals(0, trialcard.getOnTop());
    }

    @Test
    void commonGoalCard1_2NotCompletedTest()
    {
        Bookshelf commongoal1Excepted = new Bookshelf();
        CommonGoalCard trialcard = new CommonGoalCard1_2(2, 2, 6);

        assertEquals(false, trialcard.commonGoalAchieved(commongoal1Excepted));

        commongoal1Excepted.getBookshelf()[3][4]=new ItemTile(ItemTileType.CAT, 1);
        commongoal1Excepted.getBookshelf()[4][4]=new ItemTile(ItemTileType.CAT, 1);

        commongoal1Excepted.getBookshelf()[3][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[3][1]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[4][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[5][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[4][2]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[4][3]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[5][2]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[5][3]=new ItemTile(ItemTileType.GAME, 1);
        commongoal1Excepted.getBookshelf()[5][4]=new ItemTile(ItemTileType.GAME, 1);

        commongoal1Excepted.getBookshelf()[3][2]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal1Excepted.getBookshelf()[3][3]=new ItemTile(ItemTileType.PLANT, 1);

        commongoal1Excepted.getBookshelf()[4][1]=new ItemTile(ItemTileType.BOOK, 1);
        commongoal1Excepted.getBookshelf()[5][1]=new ItemTile(ItemTileType.BOOK, 1);
        boolean flag = trialcard.commonGoalAchieved(commongoal1Excepted);
        assertEquals(false, flag);
    }
}


