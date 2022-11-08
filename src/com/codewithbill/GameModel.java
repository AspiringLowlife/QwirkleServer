package com.codewithbill;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codewithbill.Tile.Color;
import static com.codewithbill.Tile.Shape;

public class GameModel implements Serializable {

    //108 tiles in the game bag
    private ArrayList<Tile> bag = new ArrayList<>();
    private ArrayList<Tile> board = new ArrayList<>();
    //2-4 players
    private ArrayList<Player> players = new ArrayList<>();
    private Integer gameID;
    public boolean isReady;
    private boolean gameDone;
    public int playerTotal = 0;
    public static int curPlayerNo = 0;
    public Player curPlayer;

    public GameModel(int playerTotal) {
        generatePieces();
        this.playerTotal = playerTotal;
        isReady = false;
        gameDone=false;
    }

    //fills bag with random pieces
    public void generatePieces() {
        //repeat this three times so each shape has three in the same color
        for (int i = 0; i < 3; i++) {

            for (int s = 0; s < 6; s++) {

                //each color gets one shape
                for (int c = 0; c < 6; c++) {
                    Tile tile = new Tile(Color.values()[c], Shape.values()[s]);
                    tile.setState(Tile.State.inBag);
                    bag.add(tile);
                }
            }
        }
        shuffle();
    }

    private void shuffle() {
        // Very basic shuffle
        Random r = new Random();
        for (int j = 0; j < 500; j++) {
            for (int i = 0; i < bag.size(); i++) {
                int randomPos = r.nextInt(bag.size());
                Tile newPiece = bag.get(randomPos);
                bag.remove(randomPos);
                bag.add(newPiece);
            }
        }
    }

    //initial hand  made
    private ArrayList<Tile> createPlayerHand() {
        ArrayList<Tile> hand = new ArrayList<>();
        Random r = new Random();
        for (int x = 0; x < 6; x++) {
            int randomPos = r.nextInt(bag.size());
            Tile yoinkedPiece = bag.get(randomPos);
            bag.remove(randomPos);
            hand.add(yoinkedPiece);
            yoinkedPiece.setState(Tile.State.inHand);
        }
        return hand;
    }

    public void changeCurPlayer() {
        if (curPlayerNo < playerTotal - 1) {
            curPlayerNo++;
        } else {
            curPlayerNo = 0;
        }
        curPlayer = players.get(curPlayerNo);
    }

    public void addToBoard(ArrayList<Tile> hand) {
        boolean placed = false;
        Stream stream = hand.stream().filter(tile -> tile.state.equals(Tile.State.placing));
        List<Tile> placeList = (List<Tile>) stream.collect(Collectors.toList());
        for (Tile tile : placeList) {
            if (tile.state.equals(Tile.State.placing)) {
                hand.remove(tile);
                tile.setState(Tile.State.onBoard);
                board.add(tile);
                placed = true;
                //Random number for points scored
                Random ran=new Random();
                int points= ran.nextInt(12);
                curPlayer.addScore(points);
            }
        }
        if (placed) {
            curPlayer.setHand(hand);
            fillHand();
        }
        //game ending condition
        if(curPlayer.getHand().isEmpty())gameDone=true;
    }

    public void swapPieces(ArrayList<Tile> hand) {
        boolean swapped = false;
        if (hand.size() > bag.size()) return;
        Stream stream = hand.stream().filter(tile -> tile.state.equals(Tile.State.swapping));

        List<Tile> swapList = (List<Tile>) stream.collect(Collectors.toList());
        for (Tile tile : swapList) {
            if (tile.state.equals(Tile.State.swapping)) {
                hand.remove(tile);
                tile.setState(Tile.State.inBag);
                bag.add(tile);
                swapped = true;
            }
        }
        if (swapped) {
            shuffle();
            curPlayer.setHand(hand);
            fillHand();
        }
    }

    public void fillHand() {
        while (curPlayer.getHand().size() < 6 && bag.size() > 0) {
            Tile newTile = bag.remove(bag.size() - 1);
            curPlayer.getHand().add(newTile);
            newTile.setState(Tile.State.inHand);
        }
    }

    public Player addPlayer() {
        if (bag.size() < 6) return null;
        Player player = new Player(createPlayerHand(),players.size());
        player.setGameID(gameID);
        players.add(player);
        if (players.size() == playerTotal) isReady = true;
        if (players.size() == 1) curPlayer = players.get(0);
        return player;
    }

    public void removePlayer(Player player) {


        ArrayList<Tile> playerHand = player.getHand();
        Player serverPlayer = null;
//        outerLoop:
        for (int i = 0; i < players.size(); i++) {
            serverPlayer = players.get(i);
            ArrayList<Tile> serverHand = serverPlayer.getHand();
            for (int j = 0; j < serverPlayer.getHand().size() - 1; j++) {
                if (!serverHand.get(j).toString().equals(playerHand.get(j).toString())) break;
            }
            break;
        }
        //put tiles back in bag
        for (Tile tile : serverPlayer.getHand()) {
            tile.setState(Tile.State.inBag);
            bag.add(tile);
        }
        shuffle();
        players.remove(serverPlayer);
    }

    public void setGameID(int gamesIndex) {
        gameID = gamesIndex;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Tile> getBoard() {
        return board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Tile> getBag() {
        return bag;
    }

    //tester Method not Apart of game
    public void fillBoard() {
        for (Player player : players) {
            for (Tile tile : player.getHand()) {
                tile.setState(Tile.State.inBag);
                bag.add(tile);
            }
        }
        for (Tile tile : bag) {
            tile.setState(Tile.State.onBoard);
            board.add(tile);
        }
        bag = new ArrayList<>();
        this.isReady=true;
    }

    public boolean isGameDone() {
        return gameDone;
    }
    public void setGameDone(){
        gameDone=true;
        isReady=true;
    }
}

