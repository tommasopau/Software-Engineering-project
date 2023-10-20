package it.polimi.ingsw.model;

/**
 * This class represents the common goal card number 9 that observes the following description:
 * Two columns each formed by six different types of tiles.
 */
public class CommonGoalCard9 extends CommonGoalCard{

    /**
     * Constructor
     * @param numberOfPlayers number of players
     */
    public CommonGoalCard9(int numberOfPlayers){
        super(numberOfPlayers);
        description = "Two columns each formed by 6 " +
                "different types of tiles.";
    }

    /**
     * Method for checking the achievement of the common goal
     * @param bookshelf player's bookshelf for the comparison
     * @return true if the common goal has been achieved
     */
    public boolean commonGoalAchieved(Bookshelf bookshelf){
        ItemTile[][] b = bookshelf.getBookshelf();
        boolean different;
        int r = 6; // Number of rows
        int c = 5; // Number of columns
        int count = 0; // Number of columns formed by different types of tiles
        // Iterating through the columns of the bookshelf
        for(int i = 0; i < c && count < 2; i++){
            different = true;
            // Checking if the column is full and if each tile is different from the following ones
            for(int j = 0; j < r && different; j++){
                if(b[j][i].isEqualType(new ItemTile(ItemTileType.EMPTY, 0))){
                    different = false;
                }else{
                    for(int k = j; k < r - 1 && different; k++){
                        if(b[j][i].isEqualType(b[k + 1][i])){
                            different = false;
                        }
                    }
                }
            }
            if(different){
                count++;
            }
        }
        if(count == 2){
            return true;
        }
        return false;
    }
}
