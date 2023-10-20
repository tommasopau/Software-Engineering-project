package it.polimi.ingsw.model;

/**
 * Common goal card that represents the following description:
 * At least eight tiles of the same type on the bookshelf, regardless of their position
 */

public class CommonGoalCard6 extends CommonGoalCard{
    private ItemTile[][] griglia;
    int[] count = new int[6];

    /**
     * Constructor for common goal number 6
     * @param numberOfPlayers the number of players
     */
    public CommonGoalCard6(int numberOfPlayers) {
        super(numberOfPlayers);
        description = "Eight tiles of the same type. There’s no " +
                "restriction about the position of these " +
                "tiles.";
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
     * keeps adding 1 to the different cells of the counter until one gets to 8
     * @param bookshelf to analyze
     * @return true if at least 8 ItemTile of the same type
     */
    public boolean commonGoalAchieved(Bookshelf bookshelf) {
        griglia = bookshelf.getBookshelf();
        count = new int[6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                int index = getIndex(griglia[i][j]);
                if (index != -1) {
                    count[index]++;
                    if (count[index] == 8) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
