package com.codewithbill;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    public static int portNum = 500;
    private final GameModel gameModel = new GameModel();
    int counter = 1;
    int uniqueID = 1;
    //DataOutputStream dos;
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
                oos=new ObjectOutputStream(connection.getOutputStream());
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

        Object request =  ois.readObject();
        //adding a new player
        if (request.getClass().equals(String.class)) {
            if (request.equals("NewPlayer")) {
                oos.writeObject(gameModel.addPlayer());
                oos.flush();
                System.out.println("New Player created");
            }
        }
        //player making a move
        else if (request.getClass().equals(Player.class)) {
            Player player= (Player) request;
            System.out.println("Receiving player move");
            //Swapping pieces
            gameModel.swapPieces(player.getHand());
            //placing on board
            gameModel.addToBoard(player.getHand());
            //Send results back to player
            oos.writeObject(new MoveResponse(gameModel.curPlayer,gameModel.getBoard()));
            oos.flush();
            System.out.println("Sending updated hand and board state back to player");
            gameModel.changeCurPlayer();
            System.out.println("Changed current player");
        }

    }
}