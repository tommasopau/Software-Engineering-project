package it.polimi.ingsw.model;

/**
 * This class represents the common goal card number 7 that observes the following description:
 * Five tiles of the same type forming a diagonal.
 */
public class CommonGoalCard7 extends CommonGoalCard{

    /**
     * Constructor
     * @param numberOfPlayers number of players 
     */
    public CommonGoalCard7(int numberOfPlayers){
        super(numberOfPlayers);
        description = "Five tiles of the same type forming a " +
                "diagonal.";
    }

    /**
     * Method for checking the achievement of the common goal
     * @param bookshelf player's bookshelf for the comparison
     * @return true if the common goal has been achieved
     */
    public boolean commonGoalAchieved(Bookshelf bookshelf) {
        ItemTile[][] b = bookshelf.getBookshelf();
        boolean same;
        // Checking if at least one of the two diagonals (from top left to bottom right)
        // is formed by the same type of tiles.
        for (int i = 0; i < 2; i++) {
            same = true;
            for (int j = 0; j < 5 && same; j++) {
                if (b[i][0].isEqualType(new ItemTile(ItemTileType.EMPTY, 0)) || !b[i + j][j].isEqualType(b[i][0])) {
                    same = false;
                }
            }
            if (same) {
                return true;
            }
        }
        // If it is not, checking if at least one of the others two diagonals (from bottom left to top right)
        // is formed by the same type of tiles.
        for (int i = 4; i < 6; i++) {
            same = true;
            for (int j = 0; j < 5 && same; j++) {
                if (b[i][0].isEqualType(new ItemTile(ItemTileType.EMPTY, 0)) || !b[i - j][j].isEqualType(b[i][0])) {
                    same = false;
                }
            }
            if (same) {
                return true;
            }
        }
        return false;
    }

}
