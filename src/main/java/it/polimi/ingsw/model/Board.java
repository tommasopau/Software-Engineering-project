package it.polimi.ingsw.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;

/**
 * This class represents the Board, which contains all the tiles picked from the Bag and where the user can take tiles
 * to place on its personal Bookshelf
 */
public class Board {
    private ItemTile[][] board;
    private boolean [][] validTiles;

    private Bag bag;


    /**
     * Constructor of Board. It reads a file called Board.txt
     * @param numOfPlayers number of players in game
     * @throws IOException related to an error while writing or reading on file
     * @throws URISyntaxException related to an error while writing or reading on file
     */

    public Board(int numOfPlayers) throws URISyntaxException, IOException {
        int rows = 0;
        int columns = 0;
        URI uri = ClassLoader.getSystemResource("Board.txt").toURI();

        FileSystem fs = null;
        if ("jar".equals(uri.getScheme())) {
            for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
                if (provider.getScheme().equalsIgnoreCase("jar")) {
                    try {
                        fs = provider.getFileSystem(uri);
                    } catch (FileSystemNotFoundException e) {
                        fs = provider.newFileSystem(uri, Collections.emptyMap());
                    }
                }
            }
        }

        List<String> fileRows = Files.lines(Paths.get(uri)).toList();
        Scanner s = new Scanner(fileRows.get(0));
        rows = s.nextInt(); //Number of rows
        columns = s.nextInt(); //Number of columns
        bag = new Bag(); //Create a new bag
        board = new ItemTile[rows][columns];
        validTiles = new boolean[rows][columns];

        for (int i = 0; i < rows; i++) {
            s = new Scanner(fileRows.get(i + 1));
            for (int j = 0; j < columns; j++) {
                board[i][j] = new ItemTile(ItemTileType.EMPTY, 0); //Initialize as an empty board

                int tile = s.nextInt();
                if (tile == 0) //Case tile outside the board
                {
                    validTiles[i][j] = false;
                } else if (tile <= numOfPlayers) //Case valid tile for a number of players
                {
                    validTiles[i][j] = true;
                } else //Case not valid tile for a number of players
                {
                    validTiles[i][j] = false;
                }
            }
        }
        s.close();
        if (fs != null) {
            fs.close();
        }

    }

    /**
     * Method for refilling the board with new tiles from the bag (without removing the ones left on the board)
     * @param gameName the game name
     * @throws IOException related to an error while writing or reading on file
     */
    public void refill(String gameName) throws IOException {
        String fileName = System.getProperty("user.dir") + "/Persistence/" + gameName + "Bag.txt";

        File file = new File(fileName);
        if (file.exists() == false) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(fileName, true);

        if (!bag.noTilesLeft()) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (validTiles[i][j] && board[i][j].getItemTileType() == ItemTileType.EMPTY && bag.noTilesLeft() == false) {
                        ItemTile itemTilePulledOut = bag.pullOutTile();
                        setTile(itemTilePulledOut, i, j);
                        writer.write(itemTilePulledOut.getItemTileType().toString() + "\n");
                        writer.write(itemTilePulledOut.getType() + "\n");
                    }
                }
            }
        }

        writer.close();
    }

    /**
     * Returns true if there are the correct conditions for the refill of the board
     * @return true if there are the correct conditions for the refill of the board, else false
     */
    public boolean refillNeeded()
    {
        boolean flag=true;
        loop:
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(validTiles[i][j] && board[i][j].getItemTileType() != ItemTileType.EMPTY){
                    if(i!=8 && validTiles[i+1][j]){
                        if(board[i+1][j].getItemTileType() != ItemTileType.EMPTY){
                            flag=false;
                            break loop;
                        }
                    }
                    if(j!=8 && validTiles[i][j+1]){
                        if(board[i][j+1].getItemTileType() != ItemTileType.EMPTY){
                            flag=false;
                            break loop;
                        }
                    }
                }

            }

        }
        return flag;
    }


    /**
     * Getter method, which returns a tile of the board given coordinates
     * @param xCoordinate row of the tile on the board
     * @param yCoordinate column of the tile on the board
     * @return the tile wanted
     */
    public ItemTile getTile(int xCoordinate, int yCoordinate){
        return board[xCoordinate][yCoordinate];
    }

    /**
     * Getter method, returns true if the given coordinates correspond to a valid space in the board
     * @param xCoordinate row of the space
     * @param yCoordinate column of the space
     * @return returns true if the given coordinates correspond to a valid space in the board
     */
    public boolean getValidTile(int xCoordinate, int yCoordinate){
        return validTiles[xCoordinate][yCoordinate];
    }

    /**
     * Set a tile, given the item and the coordinates
     * @param item the ItemTile to set
     * @param xCoordinate row of the board where the tile will be set
     * @param yCoordinate column of the board where the tile will be set
     */
    public void setTile(ItemTile item, int xCoordinate, int yCoordinate){
        board[xCoordinate][yCoordinate] = item;
    }

    /**
     * Checking if a tile is takeable (at least one side free)
     * @return a boolean matrix of the takeable tiles
     */
    public boolean[][] takeableTiles(){
        int r = 9; // Number of rows
        int c = 9; // Number of columns
        boolean[][] mat = new boolean[9][9];
        for(int i = 0; i < r; i++){
            for(int j = 0; j < c; j++){
                if(board[i][j].getItemTileType() != ItemTileType.EMPTY){
                    if(i == 4 && j == 0 || i == 0 && j == 3 || i == 0 && j == 4 || i == 5 && j == 0 || i == r - 1 && j == 4 || i == r - 1 && j == 5 || i == 3 && j == c - 1 || i == 4 && j == c - 1){
                        mat[i][j] = true;
                    }else if(board[i - 1][j].getItemTileType() == ItemTileType.EMPTY || board[i + 1][j].getItemTileType() == ItemTileType.EMPTY || board[i][j + 1].getItemTileType() == ItemTileType.EMPTY || board[i][j - 1].getItemTileType() == ItemTileType.EMPTY){
                        mat[i][j] = true;
                    }else{
                        mat[i][j] = false;
                    }
                }else if(!validTiles[i][j]){
                    mat[i][j] = false;
                }
            }
        }
        return mat;
    }

    /**
     * Calculates the maximum number of tiles that can be taken consecutively on the board
     * @return the maximum number
     */
    public int numberOfTakeableTiles()
    {
        int rows = 9;
        int columns = 9;
        int maxNumber = 0;

        boolean[][] takeableTiles = takeableTiles();

        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                //For each found takeable tile, the maximum number of consecutive tiles in the same row and column is calculated
                if(takeableTiles[i][j] == true)
                {
                    int maxNumberTileRow = 1;
                    for(int k = 1; k < 3; k++)
                    {
                        if(j + k < 9 && takeableTiles[i][j+k] == true)
                        {
                            maxNumberTileRow++;
                        }
                        else if(k == 1) //Case not consecutive
                        {
                            k = 3; //Exiting the for
                        }
                    }
                    if(maxNumberTileRow > maxNumber)
                    {
                        maxNumber = maxNumberTileRow;
                    }

                    int maxNumberTileColumn = 1;
                    for(int k = 1; k < 3; k++)
                    {
                        if(i + k < 9 && takeableTiles[i+k][j] == true)
                        {
                            maxNumberTileColumn++;
                        }
                        else if(k == 1) //Case not consecutive
                        {
                            k = 3; //Exiting the for
                        }
                    }
                    if(maxNumberTileColumn > maxNumber)
                    {
                        maxNumber = maxNumberTileColumn;
                    }
                }
            }
        }
        return maxNumber;
    }

    /**
     * Getter method for board
     * @return the board
     */
    public ItemTile[][] getBoard(){return board;}

    /**
     * Writing the contents of the board into a Persistence file
     * @param gameName the name of the game
     * @throws IOException related to an error while writing or reading on file
     */
    public void writeOnFile(String gameName) throws IOException {
        String fileName = System.getProperty("user.dir") + "/Persistence/" + gameName + "Board.txt";
        int numberOfRows = 9;
        int numberOfColumns = 9;

        File file = new File(fileName);
        if (file.exists() == false) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(fileName);

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                writer.write(board[i][j].getItemTileType().toString() + "\n");
                writer.write(board[i][j].getType() + "\n");
            }
        }
        writer.close();
    }

    /**
     * Returns the actual state of the bag
     * @return the bag
     */
    public Bag getBag() {
        return bag;
    }
}
