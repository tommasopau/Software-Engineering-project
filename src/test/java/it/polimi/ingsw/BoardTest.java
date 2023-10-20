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

public class BoardTest {
    /**
     * Testing the refill of the empty board
     */
    @Test
    void refillEmptyBoardTest() throws URISyntaxException, IOException {
        Board boardTest = new Board(3);
        boardTest.refill("TestGame");
        for(int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (boardTest.getValidTile(i,j)) {
                    assertNotEquals(new ItemTile(ItemTileType.EMPTY, 0), boardTest.getTile(i,j));
                }
            }
        }

        String fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "Bag.txt";
        File file = new File(fileName);
        file.delete();
        fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "Board.txt";
        file = new File(fileName);
        file.delete();
    }

    /**
     * Testing the refill of the board (with some tiles in it)
     */
    @Test
    void refillTest() throws URISyntaxException, IOException {
        Board boardTest = new Board(3);
        boardTest.setTile(new ItemTile(ItemTileType.BOOK, 1), 4, 3);
        boardTest.setTile(new ItemTile(ItemTileType.PLANT, 1), 4, 4);
        boardTest.setTile(new ItemTile(ItemTileType.CAT, 1), 2, 2);
        boardTest.refill("TestGame");
        for(int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (boardTest.getValidTile(i,j)) {
                    assertNotEquals(ItemTileType.EMPTY, boardTest.getTile(i,j).getItemTileType());
                    if(i == 4 && j == 3) assertEquals(ItemTileType.BOOK, boardTest.getTile(i,j).getItemTileType());
                    if(i == 4 && j == 4) assertEquals(ItemTileType.PLANT, boardTest.getTile(i,j).getItemTileType());
                    if(i == 2 && j == 2) assertEquals(ItemTileType.CAT, boardTest.getTile(i,j).getItemTileType());
                }
            }
        }
        String fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "Bag.txt";
        File file = new File(fileName);
        file.delete();
        fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "Board.txt";
        file = new File(fileName);
        file.delete();
    }

    /**
     * Testing if tiles are takeable
     */
    @Test
    void takeableTilesTest() throws URISyntaxException, IOException {
        Board boardTest = new Board(3);
        boolean[][] matTest = new boolean[9][9];

        boardTest.setTile(new ItemTile(ItemTileType.FRAME, 1), 2, 2);
        boardTest.setTile(new ItemTile(ItemTileType.GAME, 1), 2, 3);
        boardTest.setTile(new ItemTile(ItemTileType.FRAME, 1), 2, 4);
        boardTest.setTile(new ItemTile(ItemTileType.FRAME, 1), 2, 5);
        boardTest.setTile(new ItemTile(ItemTileType.FRAME, 1), 3, 1);
        boardTest.setTile(new ItemTile(ItemTileType.PLANT, 1), 3, 2);
        boardTest.setTile(new ItemTile(ItemTileType.CAT, 1), 3, 3);
        boardTest.setTile(new ItemTile(ItemTileType.GAME, 1), 3, 4);
        boardTest.setTile(new ItemTile(ItemTileType.GAME, 1), 3, 5);
        boardTest.setTile(new ItemTile(ItemTileType.TROPHIE, 1), 3, 6);
        boardTest.setTile(new ItemTile(ItemTileType.PLANT, 1), 4, 0);
        boardTest.setTile(new ItemTile(ItemTileType.PLANT, 1), 4, 1);
        boardTest.setTile(new ItemTile(ItemTileType.CAT, 1), 4, 2);
        boardTest.setTile(new ItemTile(ItemTileType.GAME, 1), 4, 3);
        boardTest.setTile(new ItemTile(ItemTileType.BOOK, 1), 4, 4);
        boardTest.setTile(new ItemTile(ItemTileType.TROPHIE, 1), 4, 5);
        boardTest.setTile(new ItemTile(ItemTileType.BOOK, 1), 4, 6);

        matTest = boardTest.takeableTiles();

        assertEquals(false, matTest[0][0]);
        assertEquals(false, matTest[1][3]);
        assertEquals(true, matTest[2][2]);
        assertEquals(true, matTest[2][3]);
        assertEquals(true, matTest[2][4]);
        assertEquals(true, matTest[2][5]);
        assertEquals(true, matTest[3][1]);
        assertEquals(false, matTest[3][2]);
        assertEquals(false, matTest[3][3]);
        assertEquals(false, matTest[3][4]);
        assertEquals(false, matTest[3][5]);
        assertEquals(true, matTest[3][6]);
        assertEquals(true, matTest[4][0]);
        assertEquals(true, matTest[4][1]);
        assertEquals(true, matTest[4][2]);
        assertEquals(true, matTest[4][3]);
        assertEquals(true, matTest[4][4]);
        assertEquals(true, matTest[4][5]);
        assertEquals(true, matTest[4][6]);
    }

    /**
     * Testing if the board returns the correct set of Tiles
     */
    @Test
    void takeTilesTest() throws IOException, URISyntaxException {
        Controller controller = new Controller();

        try {
            controller.addNewPlayer("t", 2);
            controller.getGame().setGameName("TestGame");
        }
        catch (NicknameAlreadyTakenException e){
            throw new RuntimeException(e);
        }
        catch (NicknameTooLongException e){
            throw new RuntimeException(e);
        }
        catch (FullGameException e){
            throw new RuntimeException(e);
        } catch (NicknameNotValidCharactersException e) {
            throw new RuntimeException(e);
        }

        controller.getGame().getGameBoard().getBoard()[3][2] = new ItemTile(ItemTileType.GAME, 1);
        controller.getGame().getGameBoard().getBoard()[3][3] = new ItemTile(ItemTileType.GAME, 1);
        controller.getGame().getGameBoard().getBoard()[3][4] = new ItemTile(ItemTileType.GAME, 1);
        controller.getGame().getGameBoard().getBoard()[4][2] = new ItemTile(ItemTileType.GAME, 1);
        controller.getGame().getGameBoard().getBoard()[4][3] = new ItemTile(ItemTileType.GAME, 1);
        controller.getGame().getGameBoard().getBoard()[4][4] = new ItemTile(ItemTileType.GAME, 1);
        controller.getGame().getGameBoard().getBoard()[4][5] = new ItemTile(ItemTileType.GAME, 1);
        controller.getGame().getGameBoard().getBoard()[5][4] = new ItemTile(ItemTileType.GAME, 1);
        controller.getGame().getGameBoard().getBoard()[5][5] = new ItemTile(ItemTileType.GAME, 1);
        controller.getGame().getGameBoard().getBoard()[6][5] = new ItemTile(ItemTileType.GAME, 1);
        List<ItemTile> myList = new ArrayList<>();
        List<Integer> xcor = new ArrayList<>();
        List<Integer> ycor = new ArrayList<>();
        xcor.add(2);
        ycor.add(3);
        xcor.add(4);
        ycor.add(3);
        xcor.add(3);
        ycor.add(3);
        myList.add(new ItemTile(ItemTileType.GAME, 1));
        myList.add(new ItemTile(ItemTileType.GAME, 1));
        myList.add(new ItemTile(ItemTileType.GAME, 1));

        List<ItemTile> returnList = new ArrayList<>();

        try {
            returnList = controller.takeTiles( xcor, ycor);
        } catch (NumberOfTilesNotValidException e) {
            throw new RuntimeException(e);
        } catch (NotTakeableTileException e) {
            throw new RuntimeException(e);
        } catch (NotValidConfigurationException e) {
            throw new RuntimeException(e);
        } catch (DuplicateTakeTilesException e) {
            throw new RuntimeException(e);
        } catch (NotEnoughTakeableTilesException e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i < myList.size(); i++)
        {
            assertEquals(myList.get(i).getItemTileType(), returnList.get(i).getItemTileType());
            assertEquals(myList.get(i).getType(), returnList.get(i).getType());
        }

        controller.removeFilePersistence();
        String fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "PlayerOnTurn.txt";
        File file = new File(fileName);
        file.delete();
        fileName = System.getProperty("user.dir") + "/Persistence/" + "TestGame" + "TilesTaken.txt";
        file = new File(fileName);
        file.delete();
    }

    /**
     * Testing if the board needs to be refilled
      */
    @Test
    void refillNeeded() throws URISyntaxException, IOException {
        Board boardTest=new Board(2);

        boardTest.getBoard()[2][3]= new ItemTile(ItemTileType.CAT, 1);
        boardTest.getBoard()[2][4]=new ItemTile(ItemTileType.BOOK, 1);
        boardTest.getBoard()[4][5]=new ItemTile(ItemTileType.CAT, 1);
        boardTest.getBoard() [5][3]=new ItemTile(ItemTileType.BOOK, 1);
        boardTest.getBoard()[6][4]=new ItemTile(ItemTileType.CAT, 1);
        boardTest.getBoard() [7][5]=new ItemTile(ItemTileType.BOOK, 1);
        boolean flag= boardTest.refillNeeded();
        assertEquals(false,flag);

        Board boardTest2 = new Board(2);
        boardTest2.getBoard()[4][4] = new ItemTile(ItemTileType.CAT, 1);
        boardTest2.getBoard()[3][4] = new ItemTile(ItemTileType.CAT, 1);
        assertFalse(boardTest2.refillNeeded());

        Board boardTest3 = new Board(2);
        boardTest3.getBoard()[4][4] = new ItemTile(ItemTileType.CAT, 1);
        boardTest3.getBoard()[4][3] = new ItemTile(ItemTileType.CAT, 1);
        assertFalse(boardTest3.refillNeeded());

        //The board needs the refill
        Board boardTest4 = new Board(2);
        boardTest4.getBoard()[4][4] = new ItemTile(ItemTileType.CAT, 1);
        boardTest4.getBoard()[3][3] = new ItemTile(ItemTileType.CAT, 1);
        assertTrue(boardTest4.refillNeeded());
    }

    /**
     * Testing getter method getBag()
     */
    @Test
    void getBagTest() throws URISyntaxException, IOException {
        Board boardTest = new Board(2);
        assertNotNull(boardTest.getBag());
    }

    @Test
    public void numberOfTakeableTilesTest() throws URISyntaxException, IOException {
        Board board = new Board(2);
        board.setTile(new ItemTile(ItemTileType.CAT, 1), 2, 3);
        board.setTile(new ItemTile(ItemTileType.CAT, 1), 3, 3);
        board.setTile(new ItemTile(ItemTileType.CAT, 1), 4, 3);
        assertEquals(3, board.numberOfTakeableTiles());
    }
}
