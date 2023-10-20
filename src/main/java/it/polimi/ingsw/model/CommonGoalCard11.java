package it.polimi.ingsw.model;

/**
 * Common goal card that represents the following description:
 * Five tiles of the same type disposed as X
 */

public class CommonGoalCard11 extends CommonGoalCard{
    private ItemTile[][] griglia;

    /**
     * Constructor for common goal number 11
     * @param numberOfPlayers the number of players
     */
    public CommonGoalCard11(int numberOfPlayers){
        super(numberOfPlayers);
        description = "Five tiles of the same type forming an X";
    }

    /**
     * checks the first 4 lines and 3 columns to get the starting position and look for the pattern
     * @param bookshelf to analyze
     * @return true if pattern found
     */
    public boolean commonGoalAchieved(Bookshelf bookshelf){
        griglia = bookshelf.getBookshelf();
        for(int i=0; i<4; i++){
            for(int j=0; j<3; j++){
                if(!griglia[i][j].isEqualType(new ItemTile(ItemTileType.EMPTY, 0)) &&
                griglia[i][j+2].isEqualType(griglia[i][j]) &&
                griglia[i+1][j+1].isEqualType(griglia[i][j]) &&
                griglia[i+2][j].isEqualType(griglia[i][j]) &&
                griglia[i+2][j+2].isEqualType(griglia[i][j])){
                    return true;
                }
            }
        }
        return false;
    }
}
