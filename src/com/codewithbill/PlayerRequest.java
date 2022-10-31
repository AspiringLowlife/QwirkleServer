package com.codewithbill;

import java.io.Serializable;

public class PlayerRequest implements Serializable {
    public String requestString;
    public Player player;

    public PlayerRequest(String requestString, Player player) {
        this.requestString = requestString;
        this.player = player;
    }
}
