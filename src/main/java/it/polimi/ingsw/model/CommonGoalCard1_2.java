package it.polimi.ingsw.model;

/**
 * Class used for Common Goals 1 and 2, where the goal is achieved if there are at least 'numOfGroups' groups of
 * n tiles (where n=dimension) in the bookshelf
 */
public class CommonGoalCard1_2 extends CommonGoalCard{
    private int dimension;
    private int numOfGroups;
    private ItemTile[][] griglia;

    /**
     * Constructor of the class
     * @param numberOfPlayers number of players
     * @param dimension dimension of groups
     * @param numOfGroups number of groups
     */
    public CommonGoalCard1_2(int numberOfPlayers, int dimension, int numOfGroups){
        super(numberOfPlayers);
        this.dimension = dimension;
        this.numOfGroups = numOfGroups;
        description = numOfGroups + " groups each containing at least " +
                + dimension + " tiles of the same type. " +
                "The tiles of one group can be different " +
                "from those of another group.";
    }

    /**
     * Method that returns true if the common goal is achieved or false in the other case
     * @param bookshelf to check
     * @return true if the common goal is achieved
     */
    @Override
    public boolean commonGoalAchieved(Bookshelf bookshelf){
        griglia = bookshelf.getBookshelf();
        int counter;
        counter = bookshelf.calculateNumberGroups(dimension);
        if(counter >= numOfGroups){
            return true;
        }else{
            return false;
        }
    }

}

