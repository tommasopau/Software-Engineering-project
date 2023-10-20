package it.polimi.ingsw.model;

/**
 * This class represents the common goal card number 5 that observes the following description:
 * Three columns each formed by 6 tiles of maximum three different types.
 * One column can show the same or a different combination of another column.
 */
public class CommonGoalCard5 extends CommonGoalCard{

    /**
     * Constructor
     * @param numberOfPlayers number of players
     */
    public CommonGoalCard5(int numberOfPlayers){
        super(numberOfPlayers);
        description = "Three columns each formed by 6 tiles " +
                "of maximum three different types. One " +
                "column can show the same or a different " +
                "combination of another column.";
    }

    /**
     * Method for checking the achievement of the common goal
     * @param bookshelf player's bookshelf for the comparison
     * @return true if the common goal has been achieved
     */
    public boolean commonGoalAchieved(Bookshelf bookshelf){
        ItemTile[][] b = bookshelf.getBookshelf();
        int r = 6; // Number of rows
        int c = 5; // Number of columns
        int countColumns = 0;
        int countDifferentTypes;
        boolean different; //True when two tiles have different types
        // Iterating through the columns of the bookshelf
        for(int i = 0; i < c && countColumns < 3; i++){
            countDifferentTypes = 1;
            // Checking if the column is full
            if(!b[0][i].isEqualType(new ItemTile(ItemTileType.EMPTY, 0))){
                // Iterating through the rows of the bookshelf
                for (int j = 1; j < r; j++) {
                    different = true;
                    // Checking the previous tiles if there is another tile with the same type
                    for(int k = j - 1; k >= 0 && different; k--){
                        if(b[j][i].isEqualType(b[k][i])){
                            different = false;
                        }
                    }
                    if(different){
                        countDifferentTypes++;
                    }
                }
                if(countDifferentTypes <= 3){
                    countColumns++;
                }
            }
        }
        if(countColumns == 3){
            return true;
        }
        return false;
    }
}
