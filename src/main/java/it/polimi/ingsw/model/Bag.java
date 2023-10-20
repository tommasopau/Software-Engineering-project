package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents the Bag, which contains all the tiles that have not been placed on the board yet.
 */
public class Bag {

    private List<ItemTile> remainingTiles; //List containing all tiles in the bag

    private List<ItemTile> remainingTilesType; //List containing if a certain tile (Cat1, Cat2, Cat3, Trophie1, etc.) is still in the bag

    /**
     * Constructor: it initializes the number of the tiles of each type in the Bag.
     */
    public Bag(){
        int numberOfTilesFirstType = 8;
        int numberOfTilesSecondType = 7;
        remainingTiles = new ArrayList<>();

        for(ItemTileType itemTileType : ItemTileType.values())
        {
            if(itemTileType != ItemTileType.EMPTY) {
                for (int i = 0; i < numberOfTilesFirstType; i++) {
                    remainingTiles.add(new ItemTile(itemTileType, 1));
                }
                for (int i = 0; i < numberOfTilesSecondType; i++) {
                    remainingTiles.add(new ItemTile(itemTileType, 2));
                    remainingTiles.add(new ItemTile(itemTileType, 3));
                }
            }
        }

        remainingTilesType = new ArrayList<>();

        for(ItemTileType itemTileType: ItemTileType.values())
        {
            if(itemTileType != ItemTileType.EMPTY)
            {
                remainingTilesType.add(new ItemTile(itemTileType, 1));
                remainingTilesType.add(new ItemTile(itemTileType, 2));
                remainingTilesType.add(new ItemTile(itemTileType, 3));
            }
        }
    }

    /**
     * returns true if there are no tiles in the bag.
     * @return true if there are no tiles in the bag.
     */
    public boolean noTilesLeft(){
        if(remainingTiles.size() == 0)
        {
            return true;
        }
        return false;
    }

    /**
     * Taking a casual tile from the bag
     * @return the tile
     */
    public ItemTile pullOutTile()
    {
        Random rnd = new Random();

        if(noTilesLeft() == true) //Returning an empty tile if the bag is empty
        {
            return new ItemTile(ItemTileType.EMPTY, 0);
        }

        int n = rnd.nextInt(remainingTilesType.size());
        ItemTile itemChosen = remainingTilesType.get(n); //Choosing a random tile from the bag

        for(ItemTile itemTileNew : remainingTiles)
        {
            if(itemTileNew.getItemTileType() == itemChosen.getItemTileType() && itemTileNew.getType() == itemChosen.getType())
            {
                remainingTiles.remove(itemTileNew);
                break;
            }
        }

        boolean thereIsASameTile = false;
        for(ItemTile itemTileNew : remainingTiles)
        {
            if(itemTileNew.getItemTileType() == itemChosen.getItemTileType() && itemTileNew.getType() == itemChosen.getType())
            {
                thereIsASameTile = true;
            }
        }

        if(thereIsASameTile == false)
        {
            remainingTilesType.remove(itemChosen);
        }

        return itemChosen;
    }

    /**
     * Given an ItemTile, returns how many tiles of that type remains in the bag
     * @param itemTile item wanted
     * @return the quantity
     */
    public int getRemainingTiles(ItemTile itemTile)
    {
        int counter = 0;
        for(int i = 0; i < remainingTiles.size(); i++) //Counting how many tiles are in the bag of the chosen tile
        {
            if(remainingTiles.get(i).getItemTileType() == itemTile.getItemTileType() && remainingTiles.get(i).getType() == itemTile.getType())
            {
                counter++;
            }
        }
        return counter;
    }

    /**
     * It removes the ItemTile from the bag
     * @param itemTile the tile that needs to be removed
     */
    public void removeTile(ItemTile itemTile)
    {
        for(ItemTile itemTileNew : remainingTiles)
        {
            if(itemTileNew.getItemTileType() == itemTile.getItemTileType() && itemTileNew.getType() == itemTile.getType())
            {
                remainingTiles.remove(itemTileNew);
                break;
            }
        }

        boolean thereIsASameTile = false;
        for(ItemTile itemTileNew : remainingTiles)
        {
            if(itemTileNew.getItemTileType() == itemTile.getItemTileType() && itemTileNew.getType() == itemTile.getType())
            {
                thereIsASameTile = true;
            }
        }

        if(thereIsASameTile == false)
        {
            remainingTilesType.remove(itemTile);
        }
    }
}