package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This is an abstract class that generically represents the CommonGoalCard with its scores and description.
 */
public abstract class CommonGoalCard implements Serializable {

    /**
     * The scores of the common goal card
     */
    public Deque<Integer> scores = new ArrayDeque<Integer>();

    /**
     * The description of the common goal card
     */
    public String description;

    /**
     * Constructor of class CommonGoalCard.
     * @param numberOfPlayers for adding the tokens in "scores"
     */
    public CommonGoalCard( int numberOfPlayers){

        if(numberOfPlayers == 2){
            scores.push(4);
            scores.push(8);
        }else if(numberOfPlayers == 3){
            scores.push(4);
            scores.push(6);
            scores.push(8);
        }else if(numberOfPlayers == 4){
            scores.push(2);
            scores.push(4);
            scores.push(6);
            scores.push(8);
        }
    }

    /**
     * Checks if the given bookshelf respect the conditions of the common goal
     * @param bookshelf to check
     * @return true if the given bookshelf respect the conditions of the common goal
     */
    public abstract boolean commonGoalAchieved(Bookshelf bookshelf);

    /**
     * Getting and removing the token on top of "scores"
     * @return the token
     */
    public int getOnTop(){
        if (!scores.isEmpty()){
            return scores.pop();
        }
        return 0;
    }

    /**
     * Obtains the description of the CommonGoalCard
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Getting but not removing the first token available
     * @return the first token available
     */
    public int getFirstTokenAvailable()
    {
        if(scores.isEmpty())
        {
            return 0;
        }
        return scores.getFirst();
    }
}
