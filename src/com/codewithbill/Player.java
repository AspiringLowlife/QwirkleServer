package com.codewithbill;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable, Comparable<Player>{
    private ArrayList<Tile> hand;
    private int playerID;
    private int gameID;
    private Integer score;

    public Player(ArrayList<Tile> hand,int playerID) {
        this.hand = hand;
        score = 0;
        this.playerID=playerID;
    }

    public void setGameID(int gameIndex) {
        gameID = gameIndex;
    }

    public ArrayList<Tile> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Tile> hand) {
        this.hand = hand;
    }

    public void addScore(int points) {
        score += points;
    }

    @Override
    public int compareTo(Player player) {
      if(score< player.getScore())return 1;
      else if(score== player.score)return 0;
      else return -1;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getGameID() {
        return gameID;
    }

    public Integer getScore() {
        return score;
    }


}
