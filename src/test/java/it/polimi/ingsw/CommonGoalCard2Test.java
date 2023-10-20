package it.polimi.ingsw;
import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;
public class CommonGoalCard2Test {
    /**
     * Testing the achievement of the goal number 2
     */
    @Test
    void CommonGoalCard2Test() {
        Bookshelf commongoal2Excepted = new Bookshelf();
        CommonGoalCard trialcard2 = new CommonGoalCard1_2(2, 4, 4);
        for(int i=0;i<6;i++){
            for(int j=0;j<5;j++){
                commongoal2Excepted.getBookshelf()[i][j]=new ItemTile(ItemTileType.EMPTY, 0);
            }
        }



        commongoal2Excepted.getBookshelf()[0][1]=new ItemTile(ItemTileType.TROPHIE, 1);
        commongoal2Excepted.getBookshelf()[1][1]=new ItemTile(ItemTileType.TROPHIE, 1);
        commongoal2Excepted.getBookshelf()[1][2]=new ItemTile(ItemTileType.TROPHIE, 1);
        commongoal2Excepted.getBookshelf()[2][2]=new ItemTile(ItemTileType.TROPHIE, 1);
        commongoal2Excepted.getBookshelf()[4][4]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal2Excepted.getBookshelf()[5][2]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal2Excepted.getBookshelf()[5][3]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal2Excepted.getBookshelf()[5][4]=new ItemTile(ItemTileType.PLANT, 1);

        commongoal2Excepted.getBookshelf()[0][3]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal2Excepted.getBookshelf()[0][4]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal2Excepted.getBookshelf()[1][4]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal2Excepted.getBookshelf()[2][4]=new ItemTile(ItemTileType.PLANT, 1);
        commongoal2Excepted.getBookshelf()[5][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal2Excepted.getBookshelf()[4][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal2Excepted.getBookshelf()[3][0]=new ItemTile(ItemTileType.GAME, 1);
        commongoal2Excepted.getBookshelf()[3][1]=new ItemTile(ItemTileType.GAME, 1);


        boolean flag = trialcard2.commonGoalAchieved(commongoal2Excepted);


        assertEquals(flag, true);
    }
}