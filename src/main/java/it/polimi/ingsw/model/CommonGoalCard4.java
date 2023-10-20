package it.polimi.ingsw.model;

/**
 * This class represents the common goal card number 4 that observes the following description:
 * Two groups each containing 4 tiles of the same type in a 2x2 square.
 * The tiles of one square can be different from those of the other square.
 */
public class CommonGoalCard4 extends CommonGoalCard{

    /**
     * Constructor
     * @param numberOfPlayers number of players
     */
    public CommonGoalCard4(int numberOfPlayers){
        super(numberOfPlayers);
        description = "Two groups each containing 4 tiles of " +
                "the same type in a 2x2 square. The tiles " +
                "of one square can be different from " +
                "those of the other square.";
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
        int count = 0; // Number of groups containing 4 tiles of the same type in a 2x2 square
        // Iterating through the rows of the bookshelf
        for(int i = 0; i < r - 1 && count <= 2; i++){
            // Iterating through the columns of the bookshelf
            for(int j = 0; j < c - 1 && count <= 2; j++){
                if(b[i][j].getItemTileType() != ItemTileType.EMPTY && b[i][j].isEqualType(b[i][j+1]) && b[i][j].isEqualType(b[i+1][j]) && b[i][j].isEqualType(b[i+1][j+1])){
                    if(i == 0){
                        if(j == 0){
                            if(!b[i][j].isEqualType(b[i][j+2]) && !b[i][j].isEqualType(b[i+1][j+2]) && !b[i][j].isEqualType(b[i+2][j]) && !b[i][j].isEqualType(b[i+2][j+1])){
                                count++;
                            }
                        }else if(j == c - 2){
                            if(!b[i][j].isEqualType(b[i][j-1]) && !b[i][j].isEqualType(b[i+1][j-1]) && !b[i][j].isEqualType(b[i+2][j]) && !b[i][j].isEqualType(b[i+2][j+1])){
                                count++;
                            }
                        }else{
                            if(!b[i][j].isEqualType(b[i][j-1]) && !b[i][j].isEqualType(b[i+1][j-1]) && !b[i][j].isEqualType(b[i][j+2]) && !b[i][j].isEqualType(b[i+1][j+2]) && !b[i][j].isEqualType(b[i+2][j]) && !b[i][j].isEqualType(b[i+2][j+1])){
                                count++;
                            }
                        }
                    }else if(i == r - 2){
                        if(j == 0){
                            if(!b[i][j].isEqualType(b[i-1][j]) && !b[i][j].isEqualType(b[i-1][j+1]) && !b[i][j].isEqualType(b[i][j+2]) && !b[i][j].isEqualType(b[i+1][j+2])){
                                count++;
                            }
                        }else if(j == c - 2){
                            if(!b[i][j].isEqualType(b[i-1][j]) && !b[i][j].isEqualType(b[i][j-1]) && !b[i][j].isEqualType(b[i-1][j+1]) && !b[i][j].isEqualType(b[i+1][j-1])){
                                count++;
                            }
                        }else{
                            if(!b[i][j].isEqualType(b[i-1][j]) && !b[i][j].isEqualType(b[i][j-1]) && !b[i][j].isEqualType(b[i-1][j+1]) && !b[i][j].isEqualType(b[i+1][j-1]) && !b[i][j].isEqualType(b[i][j+2]) && !b[i][j].isEqualType(b[i+1][j+2])){
                                count++;
                            }
                        }
                    }else if(j == 0){
                        if(!b[i][j].isEqualType(b[i-1][j]) && !b[i][j].isEqualType(b[i-1][j+1]) && !b[i][j].isEqualType(b[i][j+2]) && !b[i][j].isEqualType(b[i+1][j+2]) && !b[i][j].isEqualType(b[i+2][j]) && !b[i][j].isEqualType(b[i+2][j+1])){
                            count++;
                        }
                    }else if(j == c - 2){
                        if(!b[i][j].isEqualType(b[i-1][j]) && !b[i][j].isEqualType(b[i][j-1]) && !b[i][j].isEqualType(b[i-1][j+1]) && !b[i][j].isEqualType(b[i+1][j-1]) && !b[i][j].isEqualType(b[i+2][j]) && !b[i][j].isEqualType(b[i+2][j+1])){
                            count++;
                        }
                    }else{
                        if(!b[i][j].isEqualType(b[i-1][j]) && !b[i][j].isEqualType(b[i][j-1]) && !b[i][j].isEqualType(b[i-1][j+1]) && !b[i][j].isEqualType(b[i+1][j-1]) && !b[i][j].isEqualType(b[i][j+2]) && !b[i][j].isEqualType(b[i+1][j+2]) && !b[i][j].isEqualType(b[i+2][j]) && !b[i][j].isEqualType(b[i+2][j+1])){
                            count++;
                        }
                    }
                }
            }
        }
        if(count == 2){
            return true;
        }
        return false;
    }
}
