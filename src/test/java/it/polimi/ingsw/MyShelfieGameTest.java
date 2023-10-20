package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MyShelfieGameTest
{
    /**
     * Test for testing the method start();
     */
    @Test
    void distributionOfCommonGoalPersonalGoalTest() throws IOException, URISyntaxException {
        MyShelfieGame game = new MyShelfieGame(3);
        game.setGameName("TestGame");
        Player player1 = new Player("Marco");
        Player player2 = new Player("Rebecca");
        Player player3 = new Player("Tommaso");

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);

        game.start();

        assertNotEquals(null, player1.getPersonalGoalCard());
        assertNotEquals(null, player2.getPersonalGoalCard());
        assertNotEquals(null, player3.getPersonalGoalCard());

        assertEquals(2, game.getCommonGoals().size());
        for(int i = 0; i < 2; i++)
        {
            assertNotEquals(null, game.getCommonGoals().get(i));
        }

        game.addCommonGoalPersistence(0);

        String fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "Bag.txt";
        File file = new File(fileName);
        file.delete();
        fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "Board.txt";
        file = new File(fileName);
        file.delete();
        fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "CommonGoalCard.txt";
        file = new File(fileName);
        file.delete();
        fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "Game.txt";
        file = new File(fileName);
        file.delete();
        fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "PersonalGoalCard.txt";
        file = new File(fileName);
        file.delete();
    }
}
