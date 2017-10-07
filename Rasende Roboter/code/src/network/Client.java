package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Stack;

import controller.Controller;
import game.Constant;
import game.Constant.Color;
import game.Robot;

/**
 * Client side of the application
 */
public class Client {

    private InetAddress ipServer;
    DatagramSocket socket;
    private String username;
    private boolean hand;
    private int bestProposition;
    private boolean run;

    /**
     * @see Controller
     */
    private Controller controller;
    /**
     * @see Countdown
     */
    private Countdown count;

    private Thread clientThread = new Thread() {

        @Override
        public void run() {
            Client.this.listen();
        }
    };

    /**
     * Constructor of Client
     *
     * @param dest
     *            : InetAddress, server IP adress
     * @param controller
     * @param pseudo
     *            : username of the client
     * @see Controller
     */
    public Client(InetAddress dest, Controller controller, String pseudo) {
        this.count = new Countdown(controller);
        this.run = true;
        this.ipServer = dest;
        this.hand = false;
        controller.setEnabledforfeit(false);
        this.setBestProposition(0);
        this.controller = controller;
        this.username = pseudo;

        try {
            this.socket = new DatagramSocket(Constant.CLIENT_PORT);
            this.count.stopCountdown();
            controller.setOnlinePerspective(true);
            this.clientThread.start();
            System.out.println("There is a server");
        } catch (SocketException e) {
            controller.setOnlinePerspective(false);
            System.out.println("There is no server at this IPaddress");
            // e.printStackTrace();
        }
    }

    /**
     * Stop the thread of the client
     */
    public void stopClient() {
        this.run = false;
        this.socket.close();
    }

    /**
     * Listen for the data from the server
     */
    private void listen() {
        String[] data;
        byte[] buffer = new byte[15000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (this.run) {
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            data = Protocol.decodePacket(packet);

            this.processData(data);
        }
    }

    /**
     * Connection with server
     */
    public void connect() {
        DatagramPacket packet;

        try {
            String data = Protocol.encodeConnect(this.username);
            packet = Protocol.encodePacket(data, this.ipServer, Constant.SERVER_PORT);

            this.socket.send(packet);
        } catch (SocketException e) {
            System.out.println("SocketException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
    }

    /**
     * Processes the data sent by server
     *
     * @param data
     *            : String
     */
    public void processData(String[] data) {

        switch (data[0]) {
        case Protocol.GOAL_CARDS:
            this.controller.setCurrentGoal(Protocol.decodeGoalCard(data));
            break;
        case Protocol.BOARD_PIECE:
            this.controller.setBoard(Protocol.decodeBoardPiece(data));
            break;
        case Protocol.ROBOT:
            this.controller.setRobot(Protocol.decodeRobot(data));
            break;
        case Protocol.TIME:
            this.count.setTime(Protocol.decodeTime(data));
            this.controller.refreshColumn();
            break;
        case Protocol.MOVE:
            int x = Integer.parseInt(data[1]);
            int y = Integer.parseInt(data[2]);
            Color c = Color.valueOf(data[3]);

            this.controller.getgame().getRobot(c).setX(x);
            this.controller.getgame().getRobot(c).setY(y);

            if (this.controller.getgame().isWin(this.controller.getgame().getRobot(c))) {
                this.controller.getgame().startNewLap();
            }

            break;
        case Protocol.CLIENT:
            this.controller.refreshPlayers(Protocol.decodeClient(data));
            break;
        case Protocol.HAND:
            this.setHand(Protocol.decodeHand(data));
            break;
        case Protocol.PROPOSITION:
            this.bestProposition = Integer.parseInt(data[2]);
            break;
        case Protocol.REFRESH:
            this.controller.refreshBoard();
            this.controller.refreshColumn();
            break;
        case Protocol.MESSAGE:
            System.out.println(data[1]);
            break;
        case Protocol.OTHER_USERNAME:
            this.controller.stopClient(data[1] + " is not a free username, please choose an other");
            break;
        case Protocol.BUTTON_VALIDATE:
            System.out.println(data[1]);
            this.controller.setEnabledValidate(Boolean.valueOf(data[1]));
            break;
        case Protocol.SERVER_DISCONNECT:
            this.controller.stopClient("The server is offline");
            break;
        default:
            System.out.println("Flag inconnu :" + data[0]);
            break;

        }
    }

    /**
     * After a movement, send new positions of robots and update other data
     *
     * @param newRound
     *            : the current round
     * @param nbMove
     *            : the number of movements
     */
    public void sendMove(int newRound) {
        DatagramPacket packet;

        try {

            for (Robot r : this.controller.getgame().getRobots()) {
                String robotInfo = Protocol.encodeMove(r, newRound);

                packet = Protocol.encodePacket(robotInfo, this.ipServer, Constant.SERVER_PORT);
                this.socket.send(packet);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a proposition of movement's number to server
     *
     * @param username
     *            : String, his username
     * @param i
     *            : Integer, his proposition
     */
    public void sendPropositionfromPlayer(String username, Integer i) {
        String proposition = Protocol.encodeProposition(username, i);
        DatagramPacket packet = Protocol.encodePacket(proposition, this.ipServer, Constant.SERVER_PORT);

        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asks the server to the next player
     */
    public void askNextProposition() {
        this.controller.getgame().setmNextPosition(new Stack<List<Robot>>());
        this.controller.getgame().setmPreviousPosition(new Stack<List<Robot>>());

        String proposition = Protocol.encodeAskNextProposition();
        DatagramPacket packet = Protocol.encodePacket(proposition, this.ipServer, Constant.SERVER_PORT);

        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isHand() {
        return this.hand;
    }

    public void setHand(boolean hand) {
        this.hand = hand;
        this.controller.setEnabledforfeit(hand);
    }

    public int getBestProposition() {
        return this.bestProposition;
    }

    public void setBestProposition(int bestProposition) {
        this.bestProposition = bestProposition;
    }

    public Countdown getCount() {
        return this.count;
    }

    public void setCount(Countdown count) {
        this.count = count;
    }

    public Boolean isRunning() {
        return this.run;
    }
}
