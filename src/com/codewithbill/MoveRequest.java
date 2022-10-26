package com.codewithbill;

import java.io.Serializable;
import java.util.ArrayList;

//This class is used to pass player hand to the server
public class MoveRequest implements Serializable {

    public ArrayList<Tile> hand;
    public Player player;

    public MoveRequest(ArrayList<Tile> hand, Player player) {
        this.hand = hand;
        this.player=player;
    }

}
