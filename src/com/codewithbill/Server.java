package com.codewithbill;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    public static int portNum = 500;

    int counter = 1;
    //Managing new games and assigning players to their games
    int playerCount = 0;
    private List<GameModel> games = new ArrayList<>();
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public Server() {
        super();
    }

    @Override
    public void run() {
        runServer();
    }

    public void runServer() {
        ServerSocket server;
        Socket connection;

        try {
            // Step 1: Create a ServerSocket.
            server = new ServerSocket(portNum, 100);
            System.out.println("SERVER: Server started "
                    + InetAddress.getLocalHost().getHostAddress()
            );

            while (true) {

                // Step 2: Wait for a connection.
                connection = server.accept();
                System.out.println("SERVER: Connection " + counter
                        + " received from: "
                        + connection.getInetAddress().getHostName());

                // Step 3: Get dis and dos streams.
                ois = new ObjectInputStream(connection.getInputStream());
                //dos = new DataOutputStream(connection.getOutputStream());
                oos = new ObjectOutputStream(connection.getOutputStream());
                System.out.println("SERVER: Got I/O streams");

                // Step 4: Process connection.
                processConnection();

                // Step 5: Close connection.
                System.out.println("SERVER: Transmission complete. "
                        + "Closing socket.");
                connection.close();
                ++counter;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void processConnection() throws ClassNotFoundException, IOException {
        System.out
                .printf("SERVER: Sending message \"Connection successful #%d\"\n", counter);
        oos.writeObject("Connection successful #" + counter);
        oos.flush();

        Object request = ois.readObject();
        //adding a new Game
        if (request.getClass().equals(Integer.class)) {
            GameModel newGame = new GameModel((Integer) request);
            games.add(newGame);
            newGame.setGameID(games.size() - 1);
            System.out.println("New game created");
            oos.writeObject(newGame.addPlayer());
            oos.flush();
        }
        //joining existing game checking existing
        else if (request.getClass().equals(String.class)) {
            if (request.equals("JoinExisting")) {
                for (GameModel game : games) {
                    if (!game.isReady) {
                        oos.writeObject(game.addPlayer());
                        oos.flush();
                        System.out.println("New Player added to existing game");
                        return;
                    }
                }
                //no game to join but join existing still clicked
                oos.writeObject("Nope");
                oos.flush();
            } else if (request.equals("CheckExisting")) {
                for (GameModel game : games) {
                    if (!game.isReady) {
                        oos.writeObject("Yep");
                        oos.flush();
                        System.out.println("Client requested if any games available, there are");
                    }
                }
            }
        }
        //Turn Management
        //queueing for game
        //ending a game
        else if (request.getClass().equals(PlayerRequest.class)) {
            PlayerRequest playerRequest = (PlayerRequest) request;
            GameModel game = games.get(playerRequest.player.getGameID());
            switch (playerRequest.requestString) {
                //check if game is ready, used in WaitingRoom
                case "CheckIsGameReady":
                    Boolean result = game.isReady;
                    oos.writeObject(result);
                    oos.flush();
                    System.out.println("Is Game ready: " + result.toString());
                    break;
                //check turn used in MainActivity
                case "CheckTurn":
                    //if game is cancelled or finished
                    if (game.isGameDone()) {
                        oos.writeObject("GameDone");
                        oos.flush();
                        System.out.println("Game is finished");
                        return;
                    }
//                    ArrayList<Tile> requestHand = playerRequest.player.getHand();
//                    ArrayList<Tile> serverHand = game.curPlayer.getHand();
//                    for (int i = 0; i < requestHand.size(); i++) {
//                        Tile requestTile = requestHand.get(i);
//                        Tile serverTile = serverHand.get(i);
//                        if (!requestTile.toString().equals(serverTile.toString())) {
//                            oos.writeObject(false);
//                            oos.flush();
//                            return;
//                        }
//                    }
                    //it is this person's turn
                    if (playerRequest.player.getPlayerID() == game.curPlayer.getPlayerID()) {
                        oos.writeObject(new MoveResponse(game.curPlayer, game.getBoard()));
                        oos.flush();
                        System.out.println("Returned current board to players");
                    } else {
                        oos.writeObject(false);
                        oos.flush();
                    }
//                    oos.writeObject(new MoveResponse(game.curPlayer, game.getBoard()));
//                    oos.flush();
//                    System.out.println("Returned current board to players");
                    break;
                //Player clicks cancel icon
                case "LeaveGame":
                    game.removePlayer(playerRequest.player);
                    game.setGameDone();
                    System.out.println("Player removed from game");
                    break;
                //used in results screen
                case "GetAllPlayers":
                    oos.writeObject(game.getPlayers());
                    oos.flush();
                    System.out.println("Returned list of Players");
                    break;
            }
        }
        //player making a move
        else if (request.getClass().equals(Player.class)) {
            Player player = (Player) request;
            GameModel game = games.get(player.getGameID());
            System.out.println("Receiving player move");
            //Swapping pieces only swapping or placing will happen not both
            game.swapPieces(player.getHand());
            //placing on board
            game.addToBoard(player.getHand());
            //Send results back to player
            oos.writeObject(new MoveResponse(game.curPlayer, game.getBoard()));
            oos.flush();
            System.out.println("Sending updated hand and board state back to player");
            game.changeCurPlayer();
            System.out.println("Changed current player");
        }
    }
}
