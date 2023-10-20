package it.polimi.ingsw.model;

import java.io.IOException;
import java.io.Serializable;
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

/**
 * This class represents the ItemTile which is a tile with its types. These tiles are taken from the board and placed
 * into the player's bookshelf.
 */
public class ItemTile implements Serializable {
    private ItemTileType itemTileType;
    private int type;

    /**
     * Constructor: it initializes the tile with its types.
     * @param itemTileType is the general type of the tile (the object that it represent)
     * @param type is the specific type of the general one
     */
    public ItemTile(ItemTileType itemTileType, int type) {
        this.itemTileType = itemTileType;
        this.type = type;
    }

    /**
     * Getting the general type of the tile
     * @return the general type
     */
    public ItemTileType getItemTileType() {
        return itemTileType;
    }

    /**
     * Getting the specific type of the tile
     * @return the specific type
     */
    public int getType() {
        return type;
    }

    /**
     * Comparing if the general types of two tiles are equal
     * @param other the tile to compare with
     * @return true if they are equals, false otherwise
     */
    public boolean isEqualType(ItemTile other) {
        if (this.getItemTileType() == other.getItemTileType()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Getting the character that corresponds to the general type of the tile
     * @return the character
     */
    public String getCharacter() {
        try {

            if (this.getItemTileType() == ItemTileType.EMPTY) {
                return " ";
            }

            URI uri = ClassLoader.getSystemResource("ItemTileCharacters.txt").toURI();

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
            int row = 0;
            while (row != fileRows.size()) {
                s = new Scanner(fileRows.get(row));
                ItemTileType itemTileType = ItemTileType.valueOf(s.next());
                if (itemTileType == this.getItemTileType()) {
                    String letter = s.next();
                    s.close();
                    return letter;
                }
                s.next();
                row++;
            }
            s.close();
            if (fs != null) {
                fs.close();
            }
            return " ";
        } catch (URISyntaxException | IOException e) {
            System.err.println("Item tile error: " + e.getMessage());
        }
        return " ";
    }
}
