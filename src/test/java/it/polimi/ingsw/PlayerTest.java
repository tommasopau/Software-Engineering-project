package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class PlayerTest {

    /**
     * Testing method calculateFinalScore, using the example that can be found on the 4th page of the rules
     * (PersonalGoal 1, 4 and 8 points for CommonGoalCard)
     * @throws RowAndColumnNotFoundException if a tile is added in a full column
     */
    @Test
    void finalScoreTest() throws RowAndColumnNotFoundException, URISyntaxException, IOException {
        Player player1 = new Player("Marco");

        player1.getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 3), 0);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.FRAME, 2), 0);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.BOOK, 2), 0);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 0);

        player1.getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.BOOK, 1), 1);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 1);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 1);

        player1.getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 2), 2);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 2), 2);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 2);

        player1.getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.GAME, 3), 3);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.BOOK, 3), 3);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 3), 3);

        player1.getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 4);
        player1.getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 4);

        PersonalGoalCard personalGoalCard = new PersonalGoalCard(1);
        player1.setPersonalGoalCard(personalGoalCard);

        player1.getCommonScores().add(4);
        player1.getCommonScores().add(8);

        player1.calculateTotalScore();

        assertEquals(36, player1.getFinalScore());

        int placement = player1.getPlacement();
    }
}
