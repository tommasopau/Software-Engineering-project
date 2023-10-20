package it.polimi.ingsw.model;

import java.util.*;

/**
 * This class represents the common goal card number 10 that observes the following description:
 * Two lines each formed by 5 different types of tiles.One line can show the same or a different combination of the other lines.
 */

public class CommonGoalCard10 extends CommonGoalCard {
    private ItemTile[][] shelf;
    Set<ItemTileType> s =
            new HashSet<ItemTileType>();
    int found = 0;

    /**
     * Constructor for common goal number 10
     * @param numberOfPlayers the number of players
     */
    public CommonGoalCard10(int numberOfPlayers) {
        super(numberOfPlayers);
        description = "Two lines each formed by 5 different " +
                "types of tiles. One line can show the " +
                "same or a different combination of the " +
                "other line.";
    }

    /**
     * For each line it checks if it contains different Tiles.
     * There must be two lines that respect this rule.
     *
     * @param bookshelf player's bookshelf for the comparison
     * @return true if the goal is achieved,false otherwise.
     */
    @Override
    public boolean commonGoalAchieved(Bookshelf bookshelf) {
        shelf = bookshelf.getBookshelf();

        loop:
        {
            found = 0;
            for (int i = 0; i < 6; i++) {
                if (found == 2) break loop;
                for (int j = 0; j < 5; j++) {
                    if (shelf[i][j].getItemTileType() != ItemTileType.EMPTY) {
                        s.add(shelf[i][j].getItemTileType());
                    }
                }
                if (s.size() == 5) {
                    found++;
                }
                s.clear();
            }
        }
        if (found == 2) {
            return true;
        } else
            return false;
    }
}