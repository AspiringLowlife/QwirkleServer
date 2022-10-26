package com.codewithbill;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codewithbill.Tile.Color;
import static com.codewithbill.Tile.Shape;

public class GameModel {

    //108 tiles in the game bag
    private static ArrayList<Tile> bag = new ArrayList<>();
    private static ArrayList<Tile> board=new ArrayList<>();
    //2-4 players
    private static ArrayList<Player> players = new ArrayList<>();
    //player info
    public static int playerTotal = 0;
    public static int curPlayerNo = 0;
    public static Player curPlayer;

    public GameModel(int playerNo) {
        GameModel.playerTotal = playerNo;
        //generate all 108 tiles in random order in the pieces bag
        generatePieces();
        //initial hand for each player
        createPlayersHand(playerNo);
        curPlayer = players.get(0);
        //generate board
    }
    public GameModel(){
        generatePieces();
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

    //initial hand
    private void createPlayersHand(int playerNo) {
        Random r = new Random();
        for (int i = 0; i < playerNo; i++) {
            ArrayList<Tile> hand = new ArrayList<>();
            for (int x = 0; x < 6; x++) {
                int randomPos = r.nextInt(bag.size());
                Tile yoinkedPiece = bag.get(randomPos);
                bag.remove(randomPos);
                hand.add(yoinkedPiece);
                yoinkedPiece.setState(Tile.State.inHand);
            }
            Player player = new Player(hand);
            players.add(player);
        }
    }

    private ArrayList<Tile> createPlayerHand(){
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

    public int changeCurPlayer() {
        if (curPlayerNo == 0) {
            curPlayerNo++;
            curPlayer = players.get(curPlayerNo);
        } else if (curPlayerNo == 1) {
            curPlayerNo--;
            curPlayer = players.get(curPlayerNo);
        }
        return curPlayerNo;
    }

    public void swapPieces(ArrayList<Tile> hand) {
        if (hand.size() > bag.size()) return;
        ArrayList<Tile> playerHand = curPlayer.getHand();
        Stream stream = hand.stream().filter(tile -> tile.state.equals(Tile.State.swapping));

        List<Tile> swapList= (List<Tile>)stream.collect(Collectors.toList());
        for (Tile tile : swapList) {
            if (tile.state.equals(Tile.State.swapping)) {
                playerHand.remove(tile);
                tile.setState(Tile.State.inBag);
                bag.add(tile);
            }
        }
        shuffle();
        fillHand();
//        for (Tile tile : swapList) {
//            Tile newTile = bag.remove(bag.size() - 1);
//            tile.setState(Tile.State.inHand);
//            playerHand.add(newTile);
//        }
    }

    public void fillHand() {
        while (curPlayer.getHand().size() < 6 && bag.size() > 1) {
            Tile newTile = bag.remove(bag.size() - 1);
            curPlayer.getHand().add(newTile);
            newTile.setState(Tile.State.inHand);
        }
    }

    public void setBag(ArrayList<Tile> bag) {
        this.bag = bag;
    }

    public Player addPlayer(){
        if(bag.size()<6)return null;
        Player player= new Player(createPlayerHand());
        players.add(player);
        if(players.size()==1)curPlayer=players.get(0);
        return player;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Tile> getBoard(){
        return board;
    }
    public ArrayList<Player> getPlayers() {
        return players;
    }
    public ArrayList<Tile> getBag() {
        return bag;
    }
}
// public final String[] colors = {"Blue", "Green", "Red", "Yellow", "Purple", "Orange"};
//    public final String[] shapes = {"Square", "Circle", "Start", "Diamond", "Cross", "Club"};
//    public void generatePiece() {
//        //three sets of 36 tiles ctrl+alt+l
//        //the repeat to do each shape in the same color three times
//        for (int i = 0; i < 3; i++) {
//
//            for (String color : colors) {
//
//                for (String shape : shapes) {
//
//                   // Tile tile = new Tile(color, shape);
//                    //pieces.add(tile);
//                }
//            }
//        }
//        Shuffle();
//    }
