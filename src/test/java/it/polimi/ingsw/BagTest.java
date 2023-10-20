package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

/**
 * This is a class where the methods of the Bag are tested
 */
public class BagTest {
    /**
     * Testing if pullOutTile works
     */
    @Test
    void getCasualTile() {
        Bag bag = new Bag();
        assertNotEquals(new ItemTile(ItemTileType.EMPTY, 0), bag.pullOutTile());
    }

    /**
     * Testing if a bag contains 132 tiles
     */
    @Test
    void correctNumberOfTilesTest() {
        Bag bag = new Bag();
        for (int i = 0; i < 132; i++) {
            assertNotEquals(new ItemTile(ItemTileType.EMPTY, 0), bag.pullOutTile());
        }

        assertEquals(true, bag.noTilesLeft());
        ItemTile lastTile = bag.pullOutTile();
        assertEquals(lastTile.getItemTileType(), ItemTileType.EMPTY);
    }

    /**
     * Testing method getItemTileNumberType and getRemainingTiles
     */
    @Test
    void remainingTileTest() {
        Bag bag = new Bag();
        assertEquals(0, bag.getRemainingTiles(new ItemTile(ItemTileType.EMPTY, 0)));

        for (ItemTileType itemTileType : ItemTileType.values()) {
            if(itemTileType != ItemTileType.EMPTY) {
                assertEquals(8, bag.getRemainingTiles(new ItemTile(itemTileType, 1)));
                assertEquals(7, bag.getRemainingTiles(new ItemTile(itemTileType, 2)));
                assertEquals(7, bag.getRemainingTiles(new ItemTile(itemTileType, 3)));
            }
        }
    }

    /**
     * Test if the removing operation of the method removeTile works
     */
    @Test
    void removeTileTest() {
        Bag bag = new Bag();

        for (ItemTileType itemTileType : ItemTileType.values()) {
            if(itemTileType != ItemTileType.EMPTY) {
                bag.removeTile(new ItemTile(itemTileType, 1));
                assertEquals(7, bag.getRemainingTiles(new ItemTile(itemTileType, 1)));
                bag.removeTile(new ItemTile(itemTileType, 2));
                assertEquals(6, bag.getRemainingTiles(new ItemTile(itemTileType, 2)));
                bag.removeTile(new ItemTile(itemTileType, 3));
                assertEquals(6, bag.getRemainingTiles(new ItemTile(itemTileType, 3)));
            }
        }
    }

    @Test
    /**
     * Testing method getRemainingTiles
     */
    public void thereIsASameTileTest()
    {
        Bag bag = new Bag();
        ItemTile itemTile = new ItemTile(ItemTileType.CAT, 1);
        assertEquals(8, bag.getRemainingTiles(itemTile));
        bag.removeTile(itemTile);
        assertEquals(7, bag.getRemainingTiles(itemTile));
        bag.removeTile(itemTile);
        assertEquals(6, bag.getRemainingTiles(itemTile));
        bag.removeTile(itemTile);
        assertEquals(5, bag.getRemainingTiles(itemTile));
        bag.removeTile(itemTile);
        assertEquals(4, bag.getRemainingTiles(itemTile));
        bag.removeTile(itemTile);
        assertEquals(3, bag.getRemainingTiles(itemTile));
        bag.removeTile(itemTile);
        assertEquals(2, bag.getRemainingTiles(itemTile));
        bag.removeTile(itemTile);
        assertEquals(1, bag.getRemainingTiles(itemTile));
        bag.removeTile(itemTile);
        assertEquals(0, bag.getRemainingTiles(itemTile));
    }
}
