package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ControllerTest {
    @Test
    void takeTilesTest() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, NicknameNotValidCharactersException, IOException, URISyntaxException {

        Controller controller1 = new Controller();
        controller1.addNewPlayer("t",2);
        MyShelfieGame game = controller1.getGame();
        controller1.getGame().setGameName("TestGame");

        game.getGameBoard().getBoard()[3][2] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[3][3] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[3][4] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[4][2] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[4][3] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[4][4] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[4][5] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[5][4] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[5][5] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[6][5] = new ItemTile(ItemTileType.GAME, 1);
        List<ItemTile> myList = new ArrayList<>();
        List<Integer> xcor = new ArrayList<>();
        List<Integer> ycor = new ArrayList<>();
        xcor.add(2);
        ycor.add(3);
        xcor.add(3);
        ycor.add(3);
        xcor.add(4);
        ycor.add(3);
        myList.add(new ItemTile(ItemTileType.GAME, 1));
        myList.add(new ItemTile(ItemTileType.GAME, 1));
        myList.add(new ItemTile(ItemTileType.GAME, 1));

        List<ItemTile> returnList = new ArrayList<>();

        try {
            returnList = controller1.takeTiles(xcor, ycor);
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

        controller1.removeFilePersistence();
    }

    @Test
    public void takeTilesExceptionsTest() throws NicknameTooLongException, NicknameNotValidCharactersException, NicknameAlreadyTakenException, FullGameException, NotEnoughTakeableTilesException, DuplicateTakeTilesException, NotValidConfigurationException, NotTakeableTileException, NumberOfTilesNotValidException, IOException, URISyntaxException {
        Controller controller = new Controller();
        controller.addNewPlayer("t",2);
        MyShelfieGame game = controller.getGame();
        controller.getGame().setGameName("TestGame");

        List<Integer> xCoordinates = new ArrayList<>();
        List<Integer> yCoordinates = new ArrayList<>();

        boolean numberOfTilesNotValidException = false;
        try {
            controller.takeTiles(xCoordinates, yCoordinates);
        } catch (NumberOfTilesNotValidException e)
        {
            numberOfTilesNotValidException = true;
        }
        assertEquals(true, numberOfTilesNotValidException);

        game.getGameBoard().getBoard()[3][2] = new ItemTile(ItemTileType.GAME, 1);
        xCoordinates.add(1);
        xCoordinates.add(2);
        yCoordinates.add(3);
        yCoordinates.add(4);
        boolean notEnoughTakeableTilesException = false;
        try {
            controller.takeTiles(xCoordinates, yCoordinates);
        } catch (NotEnoughTakeableTilesException e)
        {
            notEnoughTakeableTilesException = true;
        }
        assertEquals(true, notEnoughTakeableTilesException);

        game.getGameBoard().getBoard()[3][3] = new ItemTile(ItemTileType.GAME, 1);
        xCoordinates.clear();
        yCoordinates.clear();
        xCoordinates.add(1);
        xCoordinates.add(1);
        yCoordinates.add(2);
        yCoordinates.add(2);
        boolean duplicateTakeTilesException = false;
        try {
            controller.takeTiles(xCoordinates, yCoordinates);
        } catch (DuplicateTakeTilesException e)
        {
            duplicateTakeTilesException = true;
        }
        assertEquals(true, duplicateTakeTilesException);

        xCoordinates.clear();
        yCoordinates.clear();
        xCoordinates.add(0);
        yCoordinates.add(0);
        boolean notTakeableTileException = false;
        try {
            controller.takeTiles(xCoordinates, yCoordinates);
        }
        catch (NotTakeableTileException e)
        {
            notTakeableTileException = true;
        }
        assertEquals(true, notTakeableTileException);

        game.getGameBoard().getBoard()[3][2] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[3][3] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[3][4] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[4][3] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[5][3] = new ItemTile(ItemTileType.GAME, 1);
        xCoordinates.clear();
        yCoordinates.clear();
        xCoordinates.add(2);
        xCoordinates.add(3);
        xCoordinates.add(3);
        yCoordinates.add(3);
        yCoordinates.add(3);
        yCoordinates.add(4);
        boolean notValidConfigurationException = false;
        try {
            controller.takeTiles(xCoordinates, yCoordinates);
        } catch (NotValidConfigurationException e)
        {
            notValidConfigurationException = true;
        }
        assertEquals(true, notValidConfigurationException);

        xCoordinates.clear();
        yCoordinates.clear();
        xCoordinates.add(2);
        xCoordinates.add(4);
        yCoordinates.add(3);
        yCoordinates.add(3);
        notValidConfigurationException = false;
        try {
            controller.takeTiles(xCoordinates, yCoordinates);
        } catch (NotValidConfigurationException e)
        {
            notValidConfigurationException = true;
        }
        assertEquals(true, notValidConfigurationException);

        xCoordinates.clear();
        yCoordinates.clear();
        xCoordinates.add(3);
        xCoordinates.add(3);
        yCoordinates.add(3);
        yCoordinates.add(5);
        notValidConfigurationException = false;
        try {
            controller.takeTiles(xCoordinates, yCoordinates);
        } catch (NotValidConfigurationException e)
        {
            notValidConfigurationException = true;
        }
        assertEquals(true, notValidConfigurationException);

        controller.removeFilePersistence();
    }

    @Test
    public void takeTilesRefillNeededTest() throws NicknameTooLongException, NicknameNotValidCharactersException, NicknameAlreadyTakenException, FullGameException, NumberOfTilesNotValidException, NotEnoughTakeableTilesException, DuplicateTakeTilesException, NotValidConfigurationException, NotTakeableTileException, IOException, URISyntaxException {
        Controller controller = new Controller();
        controller.addNewPlayer("t",2);
        MyShelfieGame game = controller.getGame();
        controller.getGame().setGameName("TestGame");
        List<Integer> xCoordinates = new ArrayList<>();
        List<Integer> yCoordinates = new ArrayList<>();

        game.getGameBoard().getBoard()[3][2] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[3][3] = new ItemTile(ItemTileType.GAME, 1);
        game.getGameBoard().getBoard()[3][4] = new ItemTile(ItemTileType.GAME, 1);

        assertEquals(controller.getGame().getGameBoard().getTile(1,3).getItemTileType(), ItemTileType.EMPTY);

        xCoordinates.add(3);
        yCoordinates.add(3);
        controller.takeTiles(xCoordinates, yCoordinates);

        assertNotEquals(controller.getGame().getGameBoard().getTile(1,3).getItemTileType(), ItemTileType.EMPTY);

        controller.removeFilePersistence();
    }

    @Test
    public void chooseOrderTest() throws NotValidNumberOrderException, DuplicateNumberOrderException, NicknameTooLongException, NicknameNotValidCharactersException, NicknameAlreadyTakenException, FullGameException, IOException, URISyntaxException {
        List<ItemTile> items = new ArrayList<>();
        items.add(new ItemTile(ItemTileType.CAT, 1));
        items.add(new ItemTile(ItemTileType.GAME, 1));
        items.add(new ItemTile(ItemTileType.BOOK, 1));

        List<ItemTile> expected = new ArrayList<>();
        expected.add(new ItemTile(ItemTileType.BOOK, 1));
        expected.add(new ItemTile(ItemTileType.CAT, 1));
        expected.add(new ItemTile(ItemTileType.GAME, 1));

        List<Integer> order = new ArrayList<>();
        order.add(2);
        order.add(0);
        order.add(1);

        Controller cTest = new Controller();
        cTest.addNewPlayer("Rebecca", 3);
        cTest.getGame().setGameName("TestGame");
        cTest.addNewPlayer("Marco", 3);
        cTest.addNewPlayer("Tommaso", 3);

        List<ItemTile> choosedOrder = new ArrayList<>();
        choosedOrder = cTest.chooseOrder(items, order);

        for(int i = 0; i < expected.size(); i++)
        {
            assertEquals(expected.get(i).getItemTileType(), choosedOrder.get(i).getItemTileType());
            assertEquals(expected.get(i).getType(), choosedOrder.get(i).getType());
        }
        //assertEquals(expected, cTest.chooseOrder(items, order));
        cTest.removeFilePersistence();
    }

    @Test
    public void chooseOrderExceptionsTest() throws NicknameTooLongException, NicknameNotValidCharactersException, NicknameAlreadyTakenException, IOException, FullGameException, DuplicateNumberOrderException, NotValidNumberOrderException, URISyntaxException {
        Controller controller = new Controller();
        controller.addNewPlayer("Marco", 2);
        controller.addNewPlayer("Rebecca", 2);
        List<ItemTile> tilesToOrder = new ArrayList<>();
        List<Integer> order = new ArrayList<>();
        boolean thrownException = false;

        order.add(-1);
        tilesToOrder.add(new ItemTile(ItemTileType.GAME, 1));
        try {
            controller.chooseOrder(tilesToOrder, order);
        } catch (NotValidNumberOrderException e)
        {
            thrownException = true;
        }
        assertEquals(true, thrownException);

        thrownException = false;
        order.clear();
        tilesToOrder.clear();
        tilesToOrder.add(new ItemTile(ItemTileType.GAME, 1));
        tilesToOrder.add(new ItemTile(ItemTileType.GAME, 1));
        order.add(1);
        order.add(1);
        try {
            controller.chooseOrder(tilesToOrder, order);
        } catch (DuplicateNumberOrderException e)
        {
            thrownException = true;
        }
        assertEquals(true, thrownException);

        controller.removeFilePersistence();
    }

    /**
     * Testing the placement of the tiles in the player's bookshelf
     * @throws RowAndColumnNotFoundException if a player tries to add a tile in a full column
     */
    @Test
    public void placeTilesTest() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, RowAndColumnNotFoundException, NotEnoughSpaceInColumnException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        Controller cTest = new Controller();
        cTest.addNewPlayer("Rebecca", 3);
        cTest.getGame().setGameName("TestGame");
        cTest.addNewPlayer("Marco", 3);
        cTest.addNewPlayer("Tommaso", 3);
        Player player = cTest.getGame().getPlayers().get(0);
        cTest.getGame().getCommonGoals().clear();
        cTest.getGame().getCommonGoals().add(new CommonGoalCard3(2));
        cTest.getGame().getCommonGoals().add(new CommonGoalCard4(2));

        List<ItemTile> items = new ArrayList<>();

        items.add(new ItemTile(ItemTileType.CAT, 1));
        items.add(new ItemTile(ItemTileType.GAME, 1));
        items.add(new ItemTile(ItemTileType.GAME, 1));

        Bookshelf bookshelf = player.getBookshelf();

        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 2) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 0);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 0);

        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 3) , 1);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 1);

        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 2) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.FRAME, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 3) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 2);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 2);

        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 2) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);

        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 2) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.TROPHIE, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.GAME, 1) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 2) , 4);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 3) , 4);

        cTest.placeTiles(player, items, 1);

        assertEquals(ItemTileType.CAT, bookshelf.getBookshelf()[3][1].getItemTileType());
        assertEquals(ItemTileType.GAME, bookshelf.getBookshelf()[2][1].getItemTileType());
        assertEquals(ItemTileType.GAME, bookshelf.getBookshelf()[1][1].getItemTileType());
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 1);

        cTest.placeTiles(cTest.getGame().getPlayers().get(1), items, 0);

        items.clear();
        items.add(new ItemTile(ItemTileType.CAT, 1));
        items.add(new ItemTile(ItemTileType.CAT, 1));
        items.add(new ItemTile(ItemTileType.CAT, 1));
        cTest.placeTiles(player, items, 0);
        assertEquals(true, cTest.getGame().getPlayers().get(0).getBookshelf().isFull());

        cTest.removeFilePersistence();
    }

    @Test
    public void placeTilesExceptionsTest() throws NicknameTooLongException, NicknameNotValidCharactersException, NicknameAlreadyTakenException, IOException, FullGameException, RowAndColumnNotFoundException, URISyntaxException {
        Controller controller = new Controller();
        controller.addNewPlayer("Marco", 2);
        controller.addNewPlayer("Rebecca", 2);
        controller.getGame().setGameName("TestGame");

        Player player = controller.getGame().getPlayers().get(0);

        List<ItemTile> items = new ArrayList<>();

        items.add(new ItemTile(ItemTileType.CAT, 1));
        items.add(new ItemTile(ItemTileType.GAME, 1));

        Bookshelf bookshelf = player.getBookshelf();

        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.BOOK, 2) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);
        bookshelf.addTile(new ItemTile(ItemTileType.CAT, 1) , 3);

        boolean thrownCatch = false;
        try {
            controller.placeTiles(player, items, 3);
        } catch (NotEnoughSpaceInColumnException e)
        {
            thrownCatch = true;
        }
        assertEquals(true, thrownCatch);
    }

    /**
     * First test for method addFirstPlayer
     * @throws NicknameTooLongException
     * @throws NicknameAlreadyTakenException
     * @throws FullGameException
     */
    @Test
    public void AddFirstPlayerTest() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        Controller controller = new Controller();
        controller.addNewPlayer("Marco", 4);
        controller.getGame().setGameName("TestGame");
        MyShelfieGame game = controller.getGame();
        assertNotEquals(null, game);
        assertEquals(1, game.getPlayers().size());

        controller.addNewPlayer("Rebecca", 3);
        assertEquals(2, game.getPlayers().size());
        assertEquals(4, controller.getNumberOfPlayers());

        controller.removeFilePersistence();
    }

    @Test
    public void AddPlayerIllegalArgumentExceptionTest() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        Controller controller = new Controller();
        boolean usedCatchIllegalArgumentException = false;

        try {
            controller.addNewPlayer(";.-", 2);
        } catch (NicknameNotValidCharactersException e)
        {
            usedCatchIllegalArgumentException = true;
        }
        assertEquals(true, usedCatchIllegalArgumentException);

        usedCatchIllegalArgumentException = false;
        try {
            controller.addNewPlayer("Marco", 1);
        }
        catch (IllegalArgumentException e)
        {
            usedCatchIllegalArgumentException = true;
        }
        assertEquals(true, usedCatchIllegalArgumentException);

        usedCatchIllegalArgumentException = false;
        try {
            controller.addNewPlayer("Marco", 5);
        }
        catch (IllegalArgumentException e)
        {
            usedCatchIllegalArgumentException = true;
        }
        assertEquals(true, usedCatchIllegalArgumentException);

        controller.removeFilePersistence();
    }

    @Test
    public void AddPlayerNicknameTooLongExceptionTest() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        Controller controller = new Controller();
        boolean usedCatchNicknameTooLong = false;

        try {
            controller.addNewPlayer("MarcoMarcoMarcoMarco", 2);
        }
        catch (NicknameTooLongException e)
        {
            usedCatchNicknameTooLong = true;
        }
        assertEquals(true, usedCatchNicknameTooLong);

        controller.removeFilePersistence();
    }

    @Test
    public void AddPlayerNicknameAlreadyTakenExceptionTest() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        Controller controller = new Controller();
        boolean usedCatchNicknameAlreadyTaken = false;

        controller.addNewPlayer("Marco", 2);
        try {
            controller.addNewPlayer("Marco", 2);
        }
        catch (NicknameAlreadyTakenException e)
        {
            usedCatchNicknameAlreadyTaken = true;
        }
        assertEquals(true, usedCatchNicknameAlreadyTaken);

        controller.removeFilePersistence();
    }

    @Test
    public void AddPlayerFullGameException() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        Controller controller = new Controller();
        boolean usedCatchFullGameException = false;

        controller.addNewPlayer("Marco", 2);
        controller.getGame().setGameName("TestGame");
        controller.addNewPlayer("Rebecca", 3);

        try {
            controller.addNewPlayer("Tommaso", 2);
        }
        catch (FullGameException e)
        {
            usedCatchFullGameException = true;
        }
        assertEquals(true, usedCatchFullGameException);

        controller.removeFilePersistence();
    }

    /**
     * Test for testing the list OrderedPlayers in game
     */
    @Test
    void casualTurnsTest() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        Controller controller = new Controller();

        controller.addNewPlayer("Marco", 4);
        controller.getGame().setGameName("TestGame");
        controller.addNewPlayer("Rebecca", 4);
        controller.addNewPlayer("Tommaso", 4);
        controller.addNewPlayer("Filippo", 4);

        for(int i = 0; i < 4; i++)
        {
            assertEquals(true, controller.getOrderedPlayers().contains(i));
        }

        controller.removeFilePersistence();
    }


    /**
     * Test for checking the correct management of the turns
     */
    @Test
    void nextPlayerTurnTest() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        Controller controller = new Controller();
        
        controller.addNewPlayer("Marco", 4);
        controller.getGame().setGameName("TestGame");
        controller.addNewPlayer("Rebecca", 2);
        controller.addNewPlayer("Tommaso", 4);
        controller.addNewPlayer("Filippo", 4);

        Player firstPlayer = controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0));
        Player secondPlayer = controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(1));
        Player thirdPlayer = controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2));
        Player fourthPlayer = controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(3));

        assertEquals(firstPlayer, controller.getPlayerOnTurn());
        assertEquals(secondPlayer, controller.getNextPlayer());

        controller.nextTurn();

        assertEquals(secondPlayer, controller.getPlayerOnTurn());
        assertEquals(thirdPlayer, controller.getNextPlayer());

        controller.nextTurn();

        assertEquals(thirdPlayer, controller.getPlayerOnTurn());
        assertEquals(fourthPlayer, controller.getNextPlayer());

        controller.nextTurn();

        assertEquals(fourthPlayer, controller.getPlayerOnTurn());
        assertEquals(firstPlayer, controller.getNextPlayer());

        controller.nextTurn();

        assertEquals(firstPlayer, controller.getPlayerOnTurn());
        assertEquals(secondPlayer, controller.getNextPlayer());

        controller.playerHasFinished();

        assertEquals(firstPlayer, controller.getPlayerOnTurn());
        assertEquals(secondPlayer, controller.getNextPlayer());

        controller.nextTurn();

        assertEquals(secondPlayer, controller.getPlayerOnTurn());
        assertEquals(thirdPlayer, controller.getNextPlayer());

        controller.nextTurn();

        assertEquals(thirdPlayer, controller.getPlayerOnTurn());
        assertEquals(fourthPlayer, controller.getNextPlayer());

        controller.nextTurn();

        assertEquals(fourthPlayer, controller.getPlayerOnTurn());
        assertEquals(null, controller.getNextPlayer());

        controller.nextTurn();

        assertEquals(null, controller.getPlayerOnTurn());
        assertEquals(null, controller.getNextPlayer());

        controller.removeFilePersistence();
    }

    /**
     * Testing method endGame, case two player tie
     * @throws NicknameTooLongException
     * @throws NicknameAlreadyTakenException
     * @throws FullGameException
     * @throws RowAndColumnNotFoundException
     */
    @Test
    void endGameTest() throws NicknameTooLongException, NicknameAlreadyTakenException, FullGameException, RowAndColumnNotFoundException, NicknameNotValidCharactersException, IOException, URISyntaxException {
        Controller controller = new Controller();
        controller.addNewPlayer("Marco", 3);
        controller.getGame().setGameName("TestGame");
        controller.addNewPlayer("Rebecca", 3);
        controller.addNewPlayer("Tommaso", 3);

        //First ordered player scores 36 points
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 3), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.FRAME, 2), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.BOOK, 2), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 0);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.BOOK, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 1);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 2), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 2), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 2);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.GAME, 3), 3);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.BOOK, 3), 3);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 3), 3);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 4);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 4);

        PersonalGoalCard personalGoalCard = new PersonalGoalCard(1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).setPersonalGoalCard(personalGoalCard);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getCommonScores().add(4);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(0)).getCommonScores().add(8);


        //Second ordered player scores 12 points
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(1)).setPersonalGoalCard(personalGoalCard);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(1)).getCommonScores().add(4);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(1)).getCommonScores().add(8);

        //Third ordered player scores 36 points
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 3), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.FRAME, 2), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.BOOK, 2), 0);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 0);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.GAME, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.BOOK, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 1);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 1);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 2), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.TROPHIE, 1), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.FRAME, 1), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 2), 2);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.PLANT, 1), 2);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 3);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.GAME, 3), 3);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.BOOK, 3), 3);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 3), 3);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 4);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getBookshelf().addTile(new ItemTile(ItemTileType.CAT, 1), 4);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).setPersonalGoalCard(personalGoalCard);

        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getCommonScores().add(4);
        controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)).getCommonScores().add(8);

        controller.endGame();

        assertEquals(controller.getGame().getWinner(), controller.getGame().getPlayers().get(controller.getOrderedPlayers().get(2)));

        controller.removeFilePersistence();
    }

    @Test
    void numberOfTakeableTilesTest() throws NicknameTooLongException, NicknameNotValidCharactersException, NicknameAlreadyTakenException, FullGameException, NumberOfTilesNotValidException, DuplicateTakeTilesException, NotValidConfigurationException, NotTakeableTileException, NotEnoughTakeableTilesException, IOException, URISyntaxException {
        Controller controller = new Controller();
        controller.addNewPlayer("Marco", 4);
        controller.getGame().setGameName("TestGame");
        controller.addNewPlayer("Rebecca", 4);
        controller.addNewPlayer("Tommaso", 4);
        controller.addNewPlayer("Filippo", 4);

        assertEquals(2, controller.getGame().getGameBoard().numberOfTakeableTiles());

        List<Integer> xCoordinates = new ArrayList<>();
        List<Integer> yCoordinates = new ArrayList<>();

        xCoordinates.add(3);
        xCoordinates.add(4);

        yCoordinates.add(0);
        yCoordinates.add(0);

        controller.takeTiles(xCoordinates, yCoordinates);

        assertEquals(3, controller.getGame().getGameBoard().numberOfTakeableTiles());

        controller.removeFilePersistence();
    }

    @Test
    void addNewPlayerPersistence() throws NicknameAlreadyTakenException, NicknameNotFoundPersistenceException, FullGameException, IOException, URISyntaxException {
        Controller controller = new Controller();
        controller.setGameWasAlreadyCreated(true);
        controller.setNumberOfPlayers(2);
        controller.createGame(2);
        Player player1 = new Player("Marco");
        controller.getGame().addPlayer(player1);
        Player player2 = new Player("Rebe");
        controller.getGame().addPlayer(player2);
        controller.getOrderedPlayers().add(0);
        controller.getOrderedPlayers().add(1);
        controller.setPlayerOnTurn(0);
        controller.getGame().setGameName("GameTest");
        controller.setLastPlayerPersistenceState("START");

        assertEquals(0, controller.getNumberOfPlayersConnected());

        controller.addNewPlayerPersistence("Marco");

        assertEquals(1, controller.getNumberOfPlayersConnected());

        boolean usedCatchNicknameTooLongException = false;
        try {
            controller.addNewPlayerPersistence("Tommaso");
        } catch (NicknameNotFoundPersistenceException e)
        {
            usedCatchNicknameTooLongException = true;
        }
        assertEquals(true, usedCatchNicknameTooLongException);

        boolean usedCatchNicknameAlreadyTakenException = false;
        try {
            controller.addNewPlayerPersistence("Marco");
        } catch (NicknameAlreadyTakenException e)
        {
            usedCatchNicknameAlreadyTakenException = true;
        }
        assertEquals(true, usedCatchNicknameAlreadyTakenException);

        controller.addNewPlayerPersistence("Rebe");

        assertEquals(2, controller.getNumberOfPlayersConnected());

        boolean usedCatchFullGameException = false;
        try {
            controller.addNewPlayerPersistence("Tommaso");
        }catch (FullGameException e)
        {
            usedCatchFullGameException = true;
        }
        assertEquals(true, usedCatchFullGameException);
        assertEquals(2, controller.getNumberOfPlayersConnected());

        assertEquals(true, controller.isGameWasAlreadyCreated());

        assertEquals("START", controller.getLastPlayerPersistenceState());

        controller.removeFilePersistence();
    }
}

