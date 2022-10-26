package com.codewithbill;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private ArrayList<Tile> hand;

    public Player(ArrayList<Tile> hand)
    {
        this.hand = hand;
    }

    public ArrayList<Tile> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Tile> hand) {
        this.hand = hand;
    }
}
