package it.polimi.ingsw;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

public class CommonGoalCard12Test {
/**
 * Testing the achievement of the goal number 12
 */
    @Test
    void CommonGoalCard12CorrectTest() {
        Bookshelf bookshelf1 = new Bookshelf();

        for (int j = 0; j < 6; j++) {
            bookshelf1.getBookshelf()[j][0] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 1; i < 6; i++) {
            bookshelf1.getBookshelf()[i][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 2; x < 6; x++) {
            bookshelf1.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 3; k < 6; k++) {
            bookshelf1.getBookshelf()[k][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 4; z < 6; z++) {
            bookshelf1.getBookshelf()[z][4] = new ItemTile(ItemTileType.CAT, 1);
        }

        CommonGoalCard12 trialcard = new CommonGoalCard12(2);
        boolean flag = trialcard.commonGoalAchieved(bookshelf1);
        assertEquals(flag, true);

        Bookshelf bookshelf2 = new Bookshelf();
        for (int j = 0; j < 6; j++) {
            bookshelf2.getBookshelf()[j][4] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 1; i < 6; i++) {
            bookshelf2.getBookshelf()[i][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 2; x < 6; x++) {
            bookshelf2.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 3; k < 6; k++) {
            bookshelf2.getBookshelf()[k][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 4; z < 6; z++) {
            bookshelf2.getBookshelf()[z][0] = new ItemTile(ItemTileType.CAT, 1);
        }

        flag = trialcard.commonGoalAchieved(bookshelf2);
        assertEquals(flag, true);

        Bookshelf bookshelf3 = new Bookshelf();
        for (int j = 0; j < 1; j++) {
            bookshelf3.getBookshelf()[j][4] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 0; i < 2; i++) {
            bookshelf3.getBookshelf()[i][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 0; x < 3; x++) {
            bookshelf3.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 0; k < 4; k++) {
            bookshelf3.getBookshelf()[k][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 0; z < 5; z++) {
            bookshelf3.getBookshelf()[z][0] = new ItemTile(ItemTileType.CAT, 1);
        }

        flag = trialcard.commonGoalAchieved(bookshelf3);
        assertEquals(flag, true);

        Bookshelf bookshelf4 = new Bookshelf();
        for (int j = 0; j < 1; j++) {
            bookshelf4.getBookshelf()[j][0] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 0; i < 2; i++) {
            bookshelf4.getBookshelf()[i][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 0; x < 3; x++) {
            bookshelf4.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 0; k < 4; k++) {
            bookshelf4.getBookshelf()[k][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 0; z < 5; z++) {
            bookshelf4.getBookshelf()[z][4] = new ItemTile(ItemTileType.CAT, 1);
        }

        flag = trialcard.commonGoalAchieved(bookshelf4);
        assertEquals(flag, true);
    }

    @Test
    public void commonGoalCard12NotCompletedTest()
    {
        Bookshelf bookshelf1 = new Bookshelf();

        for (int j = 0; j < 6; j++) {
            bookshelf1.getBookshelf()[j][0] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 1; i < 6; i++) {
            bookshelf1.getBookshelf()[i][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 2; x < 6; x++) {
            bookshelf1.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 3; k < 6; k++) {
            bookshelf1.getBookshelf()[k][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 5; z < 6; z++) {
            bookshelf1.getBookshelf()[z][4] = new ItemTile(ItemTileType.CAT, 1);
        }

        CommonGoalCard12 trialcard = new CommonGoalCard12(2);
        boolean flag = trialcard.commonGoalAchieved(bookshelf1);
        assertEquals(flag, false);

        Bookshelf bookshelf2 = new Bookshelf();
        for (int j = 0; j < 6; j++) {
            bookshelf2.getBookshelf()[j][4] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 1; i < 6; i++) {
            bookshelf2.getBookshelf()[i][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 2; x < 6; x++) {
            bookshelf2.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 3; k < 6; k++) {
            bookshelf2.getBookshelf()[k][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 5; z < 6; z++) {
            bookshelf2.getBookshelf()[z][0] = new ItemTile(ItemTileType.CAT, 1);
        }

        flag = trialcard.commonGoalAchieved(bookshelf2);
        assertEquals(flag, false);

        Bookshelf bookshelf3 = new Bookshelf();
        for (int j = 0; j < 1; j++) {
            bookshelf3.getBookshelf()[j][4] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 0; i < 2; i++) {
            bookshelf3.getBookshelf()[i][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 0; x < 3; x++) {
            bookshelf3.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 0; k < 4; k++) {
            bookshelf3.getBookshelf()[k][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 1; z < 5; z++) {
            bookshelf3.getBookshelf()[z][0] = new ItemTile(ItemTileType.CAT, 1);
        }

        flag = trialcard.commonGoalAchieved(bookshelf3);
        assertEquals(flag, false);

        Bookshelf bookshelf4 = new Bookshelf();
        for (int j = 0; j < 1; j++) {
            bookshelf4.getBookshelf()[j][0] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 0; i < 2; i++) {
            bookshelf4.getBookshelf()[i][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 0; x < 3; x++) {
            bookshelf4.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 0; k < 4; k++) {
            bookshelf4.getBookshelf()[k][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 1; z < 5; z++) {
            bookshelf4.getBookshelf()[z][4] = new ItemTile(ItemTileType.CAT, 1);
        }

        flag = trialcard.commonGoalAchieved(bookshelf4);
        assertEquals(flag, false);

        Bookshelf bookshelf5 = new Bookshelf();
        for (int j = 0; j < 2; j++) {
            bookshelf5.getBookshelf()[j][0] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 0; i < 2; i++) {
            bookshelf5.getBookshelf()[i][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 0; x < 3; x++) {
            bookshelf5.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 0; k < 4; k++) {
            bookshelf5.getBookshelf()[k][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 1; z < 5; z++) {
            bookshelf5.getBookshelf()[z][4] = new ItemTile(ItemTileType.CAT, 1);
        }

        flag = trialcard.commonGoalAchieved(bookshelf5);
        assertEquals(flag, false);

        Bookshelf bookshelf6 = new Bookshelf();
        for (int j = 0; j < 5; j++) {
            bookshelf6.getBookshelf()[j][0] = new ItemTile(ItemTileType.PLANT, 1);
        }
        for (int i = 0; i < 2; i++) {
            bookshelf6.getBookshelf()[i][1] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int x = 0; x < 3; x++) {
            bookshelf6.getBookshelf()[x][2] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int k = 0; k < 4; k++) {
            bookshelf6.getBookshelf()[k][3] = new ItemTile(ItemTileType.FRAME, 1);
        }
        for (int z = 1; z < 5; z++) {
            bookshelf6.getBookshelf()[z][4] = new ItemTile(ItemTileType.CAT, 1);
        }

        flag = trialcard.commonGoalAchieved(bookshelf6);
        assertEquals(flag, false);
    }
}



