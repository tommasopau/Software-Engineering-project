package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.RowAndColumnNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Bookshelf, which contains all the tiles that the user has taken from the Board.
 */
public class Bookshelf {
    private ItemTile[][] bookshelf;

    /**
     * Constructor of Bookshelf class, every cell is empty
     */
    public Bookshelf()
    {
        int rows = 6;
        int columns = 5;
        bookshelf = new ItemTile[rows][columns];
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                bookshelf[i][j] = new ItemTile(ItemTileType.EMPTY, 0);
            }
        }

    }

    /**
     * Add a tile in a column
     * @param item type of tile added in a column
     * @param column place to add the wanted tile
     * @throws RowAndColumnNotFoundException if a user try to add a tile in a full column
     */
    public void addTile(ItemTile item, int column) throws RowAndColumnNotFoundException {
        int i = 5;
        int firstEmpty = 5;
        boolean emptyFound = false;
        while (i >= 0) //Getting the first empty row in the column
        {
            if(bookshelf[i][column].getItemTileType() == ItemTileType.EMPTY && !emptyFound)
            {
                firstEmpty = i;
                emptyFound = true;
            }
            i--;
        }

        if(emptyFound == false) //Calling the exception if the column is full
        {
            throw new RowAndColumnNotFoundException();
        }

        bookshelf[firstEmpty][column] = item; //Placing the item
    }

    /**
     * Method for calculating the points granted from adjacent tiles
     * @return points scored
     */
    public int calculateScoreAdjacentItemTiles()
    {
        int rows = 6;
        int columns = 5;
        int totalPoints = 0;
        int numberOfAdjacentType = 0;

        List<Integer> tilesToCheckX = new ArrayList<>(); //Saving coordinates
        List<Integer> tilesToCheckY = new ArrayList<>(); //Saving coordinates
        boolean[][] tilesAlreadyCheckedTotal = new boolean[rows][columns]; //Saving which tiles have been already checked
        boolean[][] tilesAlreadyCheckedType = new boolean[rows][columns]; //Saving which tiles have been already checked but not still counted

        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                tilesAlreadyCheckedTotal[i][j] = false;
                tilesAlreadyCheckedType[i][j] = false;
            }
        }

        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                if(bookshelf[i][j].getItemTileType() == ItemTileType.EMPTY) //Empty tiles are not counted
                {
                       tilesAlreadyCheckedTotal[i][j] = true;
                }
                if(tilesAlreadyCheckedTotal[i][j] == false && bookshelf[i][j].getItemTileType() != ItemTileType.EMPTY) //Calculating a tile which has not been checked
                {
                    numberOfAdjacentType = 0;
                    boolean tileFound = false;
                    tilesAlreadyCheckedType[i][j] = true;
                    tilesAlreadyCheckedTotal[i][j] = true;
                    ItemTileType type = bookshelf[i][j].getItemTileType();
                    numberOfAdjacentType++;
                    //check down tile
                    if(i < 5 && bookshelf[i+1][j].getItemTileType() == type && tilesAlreadyCheckedTotal[i+1][j] == false)
                    {
                        tilesToCheckX.add(i+1);
                        tilesToCheckY.add(j);
                        tilesAlreadyCheckedType[i+1][j] = true;
                        tileFound = true;
                    }
                    //check right tile
                    if(j < 4 && bookshelf[i][j+1].getItemTileType() == type && tilesAlreadyCheckedTotal[i][j+1] == false)
                    {
                        tilesToCheckX.add(i);
                        tilesToCheckY.add(j+1);
                        tilesAlreadyCheckedType[i][j+1] = true;
                        tileFound = true;
                    }
                    if(tileFound == true) //If there are any adjacent tiles with the same type...
                    {
                        while (tilesToCheckX.size() > 0 && tilesToCheckY.size() > 0) //...find if there are any other adjacent with the same type of the new tiles
                        {
                            int xCoordinate = tilesToCheckX.remove(0);
                            int yCoordinate = tilesToCheckY.remove(0);

                            numberOfAdjacentType++;

                            tilesAlreadyCheckedTotal[xCoordinate][yCoordinate] = true;

                            //check left tile
                            if(yCoordinate > 0 && bookshelf[xCoordinate][yCoordinate-1].getItemTileType() == type && tilesAlreadyCheckedTotal[xCoordinate][yCoordinate-1] == false && tilesAlreadyCheckedType[xCoordinate][yCoordinate-1] == false)
                            {
                                tilesToCheckX.add(xCoordinate);
                                tilesToCheckY.add(yCoordinate-1);
                                tilesAlreadyCheckedType[xCoordinate][yCoordinate-1] = true;
                            }
                            //check down tile
                            if(xCoordinate < 5 && bookshelf[xCoordinate+1][yCoordinate].getItemTileType() == type && tilesAlreadyCheckedTotal[xCoordinate+1][yCoordinate] == false && tilesAlreadyCheckedType[xCoordinate+1][yCoordinate] == false)
                            {
                                tilesToCheckX.add(xCoordinate+1);
                                tilesToCheckY.add(yCoordinate);
                                tilesAlreadyCheckedType[xCoordinate+1][yCoordinate] = true;
                            }
                            //check right tile
                            if(yCoordinate < 4 && bookshelf[xCoordinate][yCoordinate+1].getItemTileType() == type && tilesAlreadyCheckedTotal[xCoordinate][yCoordinate+1] == false && tilesAlreadyCheckedType[xCoordinate][yCoordinate+1] == false)
                            {
                                tilesToCheckX.add(xCoordinate);
                                tilesToCheckY.add(yCoordinate+1);
                                tilesAlreadyCheckedType[xCoordinate][yCoordinate+1] = true;
                            }
                        }
                    }

                    //Calculating points
                    if(numberOfAdjacentType == 3)
                    {
                        totalPoints += 2;
                    }
                    if(numberOfAdjacentType == 4)
                    {
                        totalPoints += 3;
                    }
                    if(numberOfAdjacentType == 5)
                    {
                        totalPoints += 5;
                    }
                    if(numberOfAdjacentType >= 6)
                    {
                        totalPoints += 8;
                    }
                }
            }
        }

        return totalPoints;
    }


    /**
     * Method that checks how many groups of at least 'n' adjacent tiles are in the bookshelf
     * @param size number of adjacent tiles
     * @return number of groups of adjacent tiles
     */
    public int calculateNumberGroups(int size)
    {
        int rows = 6;
        int columns = 5;
        int counter = 0;
        int numberOfAdjacentType = 0;

        List<Integer> tilesToCheckX = new ArrayList<>(); //Saving coordinates
        List<Integer> tilesToCheckY = new ArrayList<>(); //Saving coordinates
        boolean[][] tilesAlreadyCheckedTotal = new boolean[rows][columns]; //Saving which tiles have been already checked
        boolean[][] tilesAlreadyCheckedType = new boolean[rows][columns]; //Saving which tiles have been already checked but not still counted

        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                tilesAlreadyCheckedTotal[i][j] = false;
                tilesAlreadyCheckedType[i][j] = false;
            }
        }

        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                if(bookshelf[i][j].getItemTileType() == ItemTileType.EMPTY) //Empty tiles are not counted
                {
                    tilesAlreadyCheckedTotal[i][j] = true;
                }
                if(tilesAlreadyCheckedTotal[i][j] == false && bookshelf[i][j].getItemTileType() != ItemTileType.EMPTY) //Calculating a tile which has not been checked
                {
                    numberOfAdjacentType = 0;
                    boolean tileFound = false;
                    tilesAlreadyCheckedType[i][j] = true;
                    tilesAlreadyCheckedTotal[i][j] = true;
                    ItemTileType type = bookshelf[i][j].getItemTileType();
                    numberOfAdjacentType++;

                    //check down tile
                    if(i < 5 && bookshelf[i+1][j].getItemTileType() == type && tilesAlreadyCheckedTotal[i+1][j] == false)
                    {
                        tilesToCheckX.add(i+1);
                        tilesToCheckY.add(j);
                        tilesAlreadyCheckedType[i+1][j] = true;
                        tileFound = true;
                    }
                    //check right tile
                    if(j < 4 && bookshelf[i][j+1].getItemTileType() == type && tilesAlreadyCheckedTotal[i][j+1] == false)
                    {
                        tilesToCheckX.add(i);
                        tilesToCheckY.add(j+1);
                        tilesAlreadyCheckedType[i][j+1] = true;
                        tileFound = true;
                    }
                    if(tileFound == true) //If there are any adjacent tiles with the same type...
                    {
                        while (tilesToCheckX.size() > 0 && tilesToCheckY.size() > 0) //...find if there are any other adjacent with the same type of the new tiles
                        {
                            int xCoordinate = tilesToCheckX.remove(0);
                            int yCoordinate = tilesToCheckY.remove(0);

                            numberOfAdjacentType++;

                            tilesAlreadyCheckedTotal[xCoordinate][yCoordinate] = true;

                            //check left tile
                            if(yCoordinate > 0 && bookshelf[xCoordinate][yCoordinate-1].getItemTileType() == type && tilesAlreadyCheckedTotal[xCoordinate][yCoordinate-1] == false && tilesAlreadyCheckedType[xCoordinate][yCoordinate-1] == false)
                            {
                                tilesToCheckX.add(xCoordinate);
                                tilesToCheckY.add(yCoordinate-1);
                                tilesAlreadyCheckedType[xCoordinate][yCoordinate-1] = true;
                            }
                            //check down tile
                            if(xCoordinate < 5 && bookshelf[xCoordinate+1][yCoordinate].getItemTileType() == type && tilesAlreadyCheckedTotal[xCoordinate+1][yCoordinate] == false && tilesAlreadyCheckedType[xCoordinate+1][yCoordinate] == false)
                            {
                                tilesToCheckX.add(xCoordinate+1);
                                tilesToCheckY.add(yCoordinate);
                                tilesAlreadyCheckedType[xCoordinate+1][yCoordinate] = true;
                            }
                            //check right tile
                            if(yCoordinate < 4 && bookshelf[xCoordinate][yCoordinate+1].getItemTileType() == type && tilesAlreadyCheckedTotal[xCoordinate][yCoordinate+1] == false && tilesAlreadyCheckedType[xCoordinate][yCoordinate+1] == false)
                            {
                                tilesToCheckX.add(xCoordinate);
                                tilesToCheckY.add(yCoordinate+1);
                                tilesAlreadyCheckedType[xCoordinate][yCoordinate+1] = true;
                            }
                        }
                    }

                    if (numberOfAdjacentType >= size) {
                        counter++;
                    }
                }
            }
        }

        return counter;
    }



    /**
     * Checks if the bookshelf is full (no empty tiles left)
     * @return true if bookshelf is full, false otherwise
     */
    public boolean isFull()
    {
        boolean confirm = true;
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                if (bookshelf[i][j].getItemTileType() == ItemTileType.EMPTY) { //If there is a not empty tile, the bookshelf is not full
                    confirm = false;
                    break;
                }
            }
        }
        return confirm;
    }

    /**
     * Method for returning the max number of empty tiles in a column
     * @return max number of empty tiles in a column
     */
    public int numberOfPlaceableTiles()
    {
        int numberOfRows = 6;
        int numberOfColumns = 5;
        int maxNumber = 0;

        for(int i = 0; i < numberOfColumns; i++)
        {
            int freeColumns = 0;
            for(int j = 0; j < numberOfRows; j++)
            {
                if(bookshelf[j][i].getItemTileType() == ItemTileType.EMPTY)
                {
                    freeColumns++;
                }
            }
            if(freeColumns > maxNumber)
            {
                maxNumber = freeColumns;
            }
        }
        return maxNumber;
    }

    /**
     * Getter method for the bookshelf
     * @return attribute bookshelf
     */
    public ItemTile[][] getBookshelf(){
        return bookshelf;
    }
}