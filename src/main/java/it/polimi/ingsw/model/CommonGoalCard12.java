package it.polimi.ingsw.model;
/**
 *  This class represents the common goal card number 12 that observes the following description:
 *  Five columns of increasing or decreasing height.Starting from the first column on the left or on the right,
 *  each next column must be made of exactly one more tile.
 *
 */

public class CommonGoalCard12 extends CommonGoalCard{
    private ItemTile[][] shelf;
    private int x;

    /**
     * Constructor of the class
     * @param numberOfPlayers number of players
     */
    public CommonGoalCard12(int numberOfPlayers) {
        super(numberOfPlayers);
        description = "Five columns of increasing or decreasing " +
                "height. Starting from the first column on " +
                "the left or on the right, each next column " +
                "must be made of exactly one more tile. " +
                "Tiles can be of any type. ";
    }

    /**
     * Method that returns the number of Tiles in a column
     * @param column the column
     * @return number of tiles in a column
     */
    private int count(int column) {
        int k = 0;
        for (x = 0; x < 6; x++) {
            if (!shelf[x][column].isEqualType(new ItemTile(ItemTileType.EMPTY, 0))) {
                k++;
            }
        }
        return k;
    }

    /**
     * Method that verifies if the common goal is achieved,it analyzes each column in order to check the scheme
     * of the common goal.
     * @param bookshelf to check
     * @return true if the common goal is achieved
     */
    public boolean commonGoalAchieved(Bookshelf bookshelf) {
        shelf = bookshelf.getBookshelf();
        int i;
        boolean valid = false;
        if (count(0) == 1) {
            int k = 1;
            for (i = 1; i < 5; i++) {
                k++;
                if (count(i) == k) {
                    valid = true;
                } else {
                    valid = false;
                    break;
                }

            }
            return valid;

        }
        if (count(0) == 2) {
            int k = 2;
            for (i = 1; i < 5; i++) {
                k++;
                if (count(i) == k) {
                    valid = true;
                } else {
                    valid = false;
                    break;
                }

            }
            return valid;

        }
        if (count(0) == 6) {
            int k = 6;
            for (i = 1; i < 5; i++) {
                k--;
                if (count(i) == k) {
                    valid = true;
                } else {
                    valid = false;
                    break;
                }

            }
            return valid;

        }
        if (count(0) == 5) {
            int k = 5;
            for (i = 1; i < 5; i++) {
                k--;
                if (count(i) == k) {
                    valid = true;
                } else {
                    valid = false;
                    break;
                }

            }
            return valid;
        }
        return false;
    }
}


