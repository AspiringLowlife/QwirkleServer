package com.codewithbill;

import java.io.Serializable;
import java.util.ArrayList;

//This class is used to pass player hand to the server
public class MoveResponse implements Serializable {

    public ArrayList<Tile> board;
    public Player player;

    public MoveResponse(Player player, ArrayList<Tile> board) {
        this.player = player;
        this.board=board;
    }

    public void setBoard(ArrayList<Tile> board) {
        this.board = board;
    }
}
