package it.polimi.ingsw.model;

/**
 * Common goal card that represents the following description:
 * Four tiles of the same type placed on the corners of the bookshelf
 */

public class CommonGoalCard3 extends CommonGoalCard{
    private ItemTile[][] griglia;

    /**
     * Constructor for common goal number 3
     * @param numberOfPlayers the number of players
     */
    public CommonGoalCard3(int numberOfPlayers) {
        super(numberOfPlayers);
        description = "Four tiles of the same type in the four corners of the bookshelf.";
    }

    /**
     * checks the type of ItemTiles placed on the corners
     * @param bookshelf to analyze
     * @return true if condition met
     */
    public boolean commonGoalAchieved(Bookshelf bookshelf) {
        griglia = bookshelf.getBookshelf();

        if(griglia[0][0].getItemTileType() != ItemTileType.EMPTY &&
        griglia[0][0].isEqualType(griglia[0][4]) &&
        griglia[0][0].isEqualType(griglia[5][0]) &&
        griglia[0][0].isEqualType(griglia[5][4])){
            return true;
        }else{
            return false;
        }
    }
}
