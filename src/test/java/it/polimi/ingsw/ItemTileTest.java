package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class ItemTileTest {
    @Test
    void getCharacterTest()
    {
        ItemTile cat = new ItemTile(ItemTileType.CAT, 1);
        ItemTile trophie = new ItemTile(ItemTileType.TROPHIE, 1);
        ItemTile game = new ItemTile(ItemTileType.GAME, 1);
        ItemTile plant = new ItemTile(ItemTileType.PLANT, 1);
        ItemTile frame = new ItemTile(ItemTileType.FRAME, 1);
        ItemTile book = new ItemTile(ItemTileType.BOOK, 1);
        ItemTile empty = new ItemTile(ItemTileType.EMPTY, 0);
        assertEquals("C", cat.getCharacter());
        assertEquals("T", trophie.getCharacter());
        assertEquals("G", game.getCharacter());
        assertEquals("P", plant.getCharacter());
        assertEquals("F", frame.getCharacter());
        assertEquals("B", book.getCharacter());
        assertEquals(" ", empty.getCharacter());
    }
}
