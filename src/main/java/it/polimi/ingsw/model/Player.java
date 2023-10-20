package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent the Player
 */
public class Player {
    private String nickname;
    private int placement;
    private Bookshelf bookshelf;
    private PersonalGoalCard personalGoalCard;
    private boolean firstPlayerSeat;
    private int finalScore;
    private List<Integer> commonScores;
    private boolean finalToken;

    /**
     * Constructor of class Player, it initializes the nickname, the common scores
     * @param nickname of the player
     */
    public Player(String nickname)
    {
        this.nickname = nickname;
        commonScores = new ArrayList<>();
        commonScores.add(0);
        commonScores.add(0);
        bookshelf = new Bookshelf();
        firstPlayerSeat = false;
        finalToken = false;
    }

    /**
     * Calculating final score of a player (Personal Goal, Common Goals, Final Token, Adjacent Tiles)
     */
    public void calculateTotalScore(){
        int personalGoalPoints = personalGoalCard.calculatePersonalGoalPoints(bookshelf); //Personal Goal
        int commonGoalPoints = 0;
        for(Integer score : commonScores) //Common Goal
        {
            commonGoalPoints+= score;
        }
        int adjacentPoints = bookshelf.calculateScoreAdjacentItemTiles(); //Adjacent tiles
        int finalTokenPoint = 0;
        if(finalToken == true) //Final Token
        {
            finalTokenPoint = 1;
        }
        setFinalScore(personalGoalPoints + commonGoalPoints + adjacentPoints + finalTokenPoint);
    }

    /**
     * Setter method for finalScore
     * @param finalScore the final score
     */
    private void setFinalScore(int finalScore) {this.finalScore = finalScore;}

    /**
     * Setter method for firstPlayerSeat
     * @param firstPlayerSeat if the player is the first in turn
     */
    public void setFirstPlayerSeat(boolean firstPlayerSeat) {this.firstPlayerSeat = firstPlayerSeat;}

    /**
     * Getter method for personalGoalCard
     * @return the personalGoalCard
     */
    public PersonalGoalCard getPersonalGoalCard() {return this.personalGoalCard;}

    /**
     * Setter method for finalToken
     * @param finalToken true if the player completes its bookshelf first (gaining an extra point), false otherwise
     */
    public void setFinalToken(boolean finalToken) {
        this.finalToken = finalToken;
    }

    /**
     * Getter method for finalScore
     * @return the finalScore
     */
    public int getFinalScore() {return this.finalScore;}

    /**
     * Getter method for obtaining the nickname of the player
     * @return the nickname
     */
    public String getNickname(){return nickname;}

    /**
     * Setter method for placement
     * @param placement of the player
     */
    public void setPlacement(int placement){
        this.placement = placement;
    }

    /**
     * Setting a personal goal card
     * @param personalGoalCard of the player
     */
    public void setPersonalGoalCard(PersonalGoalCard personalGoalCard){
        this.personalGoalCard = personalGoalCard;
    }

    /**
     * Getter method for bookshelf
     * @return the bookshelf
     */
    public Bookshelf getBookshelf(){
        return bookshelf;
    }

    /**
     * Getter method for commonScores
     * @return a list containing the common scores
     */
    public List<Integer> getCommonScores(){
        return commonScores;
    }

    /**
     * Getter method for the placement
     * @return the placement
     */
    public int getPlacement() {
        return placement;
    }
}
