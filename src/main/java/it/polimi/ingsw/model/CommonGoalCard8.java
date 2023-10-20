package it.polimi.ingsw.model;

/**
 * Common goal card that represents the following description:
 * Four complete lines formed by one, two or three different types of tiles
 */

public class CommonGoalCard8 extends CommonGoalCard{
    private ItemTile[][] griglia;
    int count = 0;
    int[] counterRiga = new int[6];

    /**
     * Constructor for common goal number 8
     * @param numberOfPlayers the number of players
     */
    public CommonGoalCard8(int numberOfPlayers) {
        super(numberOfPlayers);
        description = "Four lines each formed by 5 tiles of " +
                "maximum three different types. One " +
                "line can show the same or a different " +
                "combination of another line.";
    }

    /**
     * used to create the counter array
     * @param item a position on the bookshelf
     * @return index depending on the ItemTile type
     */
    private int getIndex(ItemTile item) {
        if (item.isEqualType(new ItemTile(ItemTileType.CAT, 1))) {
            return 0;
        } else if (item.isEqualType(new ItemTile(ItemTileType.BOOK, 1))) {
            return 1;
        } else if (item.isEqualType(new ItemTile(ItemTileType.GAME, 1))) {
            return 2;
        } else if (item.isEqualType(new ItemTile(ItemTileType.FRAME, 1))) {
            return 3;
        } else if (item.isEqualType(new ItemTile(ItemTileType.TROPHIE, 1))) {
            return 4;
        } else if (item.isEqualType(new ItemTile(ItemTileType.PLANT, 1))) {
            return 5;
        } else {
            return -1;
        }
    }

    /**
     * counts the number of each type of ItemTiles for every line, then checks the number of empty cases
     * @param bookshelf to analyze
     * @return true if four complete lines formed by 1/2/3 types of ItemTiles
     */
    public boolean commonGoalAchieved(Bookshelf bookshelf) {
        griglia = bookshelf.getBookshelf();
        boolean valid;
        count = 0;
        for(int i=0; i<6; i++) {
            for (int h = 0; h < 6; h++) { //counter reset
                counterRiga[h] = 0;
            }
            int counterZero=0;
            valid = true;
            for (int j = 0; j < 5; j++) {
                int index = getIndex(griglia[i][j]);
                if (griglia[i][j].isEqualType(new ItemTile(ItemTileType.EMPTY, 0))) { //check not empty
                    valid = false;
                    break;
                }
                if(index!=-1) counterRiga[index]++;
            }
            if (valid) {
                for(int k=0; k<6; k++){
                    if(counterRiga[k]==0){
                        counterZero++;
                    }
                }
                if(counterZero>=3){
                    count++;
                }
            }
        }
        return count == 4;
    }
}
