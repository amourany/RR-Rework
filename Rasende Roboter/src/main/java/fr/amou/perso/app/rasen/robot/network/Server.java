package fr.amou.perso.app.rasen.robot.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import fr.amou.perso.app.rasen.robot.controller.Controller;
import fr.amou.perso.app.rasen.robot.game.BoardPiece;
import fr.amou.perso.app.rasen.robot.game.Constant;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Game;
import fr.amou.perso.app.rasen.robot.game.Player;
import fr.amou.perso.app.rasen.robot.game.Robot;

/**
 * Server side of the application
 */
public class Server {

    private String username;
    private boolean hand;
    private int round;
    private int bestProposition;
    private boolean run;
    private boolean startCount;
    private boolean buttonValidate;
    private DatagramSocket socket;

    /**
     * @see Game
     */
    private Game game;
    /**
     * @see PlayerManager
     */
    private PlayerManager manager;
    /**
     * @see Countdowwn
     */
    private Countdown count;
    /**
     * @see Controller
     */
    private Controller controller;

    private Thread serverThread = new Thread() {
        @Override
        public void run() {
            Server.this.listen();
        }
    };

    /**
     * Constructor of Server
     *
     * @param controller
     * @param pseudo
     *            : username that hosts the server
     * @see Controller
     */
    public Server(Controller controller, String pseudo) {
        this.run = true;
        this.count = new Countdown(controller);
        this.game = new Game(controller);
        this.hand = false;
        this.startCount = false;
        this.buttonValidate = true;
        controller.setEnabledforfeit(false);
        this.round = 1;
        this.bestProposition = 0;
        this.manager = new PlayerManager();
        this.username = pseudo;
        this.controller = controller;

        try {
            this.socket = new DatagramSocket(Constant.SERVER_PORT);
            Player p = new Player(this.socket.getInetAddress(), this.username);
            p.setHost(true);
            this.manager.addPlayer(p);
            this.sendMessage("The server is launched! ");
            this.controller.refreshPlayers(p.getPseudo() + "(" + p.getPoints() + "pts) : " + p.getNbMoveProposed()
                    + " movements proposed\n");
            this.controller.refreshColumn();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.serverThread.start();
    }

    /**
     * Stop the thread of the server
     */
    public void stopServer() {
        this.count.stopCountdown();
        this.run = false;
        this.socket.close();
    }

    /**
     * Listen for the data from clients
     */
    private void listen() {
        String[] data;
        boolean newplayer = true;
        try {
            byte[] buffer = new byte[1500];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (this.run) {
                this.socket.receive(packet);

                data = Protocol.decodePacket(packet);

                if (data[0].equals(Protocol.CONNECT)) {

                    // check if client username is free
                    for (Player p : this.manager.getPlayers()) {
                        if (data[1].equals(p.getPseudo())) {
                            newplayer = false;
                            this.sendOtherUserName(packet.getAddress(), data[1]);
                        }
                    }

                    if (newplayer) {
                        this.manager.addPlayer(new Player(packet.getAddress(), data[1]));
                        this.sendBoard(packet.getAddress());
                        this.sendRobots(packet.getAddress());
                        this.sendCard(packet.getAddress());
                        this.sendTime(packet.getAddress());
                        this.updatePlayers();
                        this.sendRefresh(packet.getAddress());
                    } else {
                        newplayer = true;
                    }
                }
                this.processData(data);

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the data sent by clients
     *
     * @param data
     *            : String
     */
    private void processData(String[] data) {
        Color c;
        int roundTmp = 1;

        switch (data[0]) {
        case Protocol.MOVE:

            int x = Integer.parseInt(data[1]);
            int y = Integer.parseInt(data[2]);
            c = Color.valueOf(data[3]);
            this.round = Integer.parseInt(data[4]);

            roundTmp = this.game.getCurrentRound();

            this.game.getRobot(c).setX(x);
            this.game.getRobot(c).setY(y);

            if (this.game.isWin(this.game.getRobot(c))) {
                this.game.startNewLap();
            }

            if (this.round != roundTmp) {
                this.addPoint();
                Protocol.encodeGoalCard(this.game.getmGoalCards(), this.game.getCurrentGoal());
                this.round = this.game.getCurrentRound();
                this.resetPropositions();
            }

            this.controller.refreshBoard();
            this.controller.refreshColumn();
            this.sendUpdates();
            break;

        case Protocol.PROPOSITION:
            this.updatePropositionfromPlayer(data[1], Integer.parseInt(data[2]));
            break;
        case Protocol.NEXTPROPOSITION:
            this.nextPlayerProposition();
            break;

        default:
            break;
        }

    }

    /**
     * Updates the list of players for the server and all clients
     */
    private void updatePlayers() {

        String data = "";
        DatagramPacket packet;

        // Update
        for (Player p2 : this.manager.getPlayers()) {
            data = Protocol.encodeClient(Arrays.asList(this.manager.getPlayers()));
            // If client
            if (!p2.isHost()) {
                packet = Protocol.encodePacket(data, p2.getIp(), Constant.CLIENT_PORT);

                try {
                    this.socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // If Server
            else if (p2.isHost()) {
                data = data.substring(Protocol.CLIENT.length() + 1); // substract "client&"
                this.controller.refreshPlayers(data);
            }
        }
    }

    /**
     * Add one point to the player who has the hand
     */
    public void addPoint() {
        for (Player p : this.manager.getPlayers()) {
            if (p.isHand()) {
                p.setPoints(p.getPoints() + 1);
                this.sendMessage(p.getPseudo() + " win the round (+1pts)");
            }
        }

    }

    /**
     * Send the new board to all clients
     */
    public void sendBoardToAllClients() {
        for (Player p : this.manager.getPlayers()) {
            if (!p.isHost()) {
                this.sendBoard(p.getIp());
                this.sendRobots(p.getIp());
                this.sendCard(p.getIp());
                this.sendTime(p.getIp());
                this.sendRefresh(p.getIp());
            }
        }
    }

    /**
     * Send the new board to one client
     *
     * @param ip
     *            : InetAddress
     */
    private void sendBoard(InetAddress ip) {
        String boardInfo;
        DatagramPacket packet;
        try {
            for (BoardPiece bp : this.game.getBoard().getBoardPieces()) {
                boardInfo = Protocol.encodeBoardPiece(bp);

                packet = Protocol.encodePacket(boardInfo, ip, Constant.CLIENT_PORT);
                this.socket.send(packet);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param ip
     *            : InetAddress
     */
    private void sendOtherUserName(InetAddress ip, String username) {
        String Info;
        DatagramPacket packet;
        try {
            Info = Protocol.encodeOtherUserName(username);

            packet = Protocol.encodePacket(Info, ip, Constant.CLIENT_PORT);
            this.socket.send(packet);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send cards and goalcard to client
     *
     * @param ip
     *            : InetAddress
     */
    private void sendCard(InetAddress ip) {
        String cardInfo = Protocol.encodeGoalCard(this.game.getmGoalCards(), this.game.getCurrentGoal());
        DatagramPacket packet = Protocol.encodePacket(cardInfo, ip, Constant.CLIENT_PORT);

        try {
            this.socket.send(packet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Send the starting position of all the robots
     *
     * @param ip
     *            : InetAddress
     */
    private void sendRobots(InetAddress ip) {
        DatagramPacket packet;
        String robotInfo;
        try {
            for (Robot r : this.game.getRobots()) {
                robotInfo = Protocol.encodeRobot(r);

                packet = Protocol.encodePacket(robotInfo, ip, Constant.CLIENT_PORT);

                this.socket.send(packet);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send the current time of the countdown to one client
     *
     * @param ip
     *            : InetAddress
     */
    public void sendTime(InetAddress ip) {
        try {
            String timeInfo = Protocol.encodeTime(this.count);

            DatagramPacket packet = Protocol.encodePacket(timeInfo, ip, Constant.CLIENT_PORT);

            this.socket.send(packet);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send the current time of the countdown to all clients
     */
    public void sendTimeToAllClients() {
        for (Player p : this.manager.getPlayers()) {
            if (!p.isHost()) {
                this.sendTime(p.getIp());
            }
        }
    }

    /**
     * Ask all clients to refresh their screen
     *
     * @param ip
     *            : InetAddress
     */
    private void sendRefresh(InetAddress ip) {
        String data = Protocol.REFRESH;
        DatagramPacket packet = Protocol.encodePacket(data, ip, Constant.CLIENT_PORT);

        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send Updates to all clients when there is a movement
     */
    public void sendUpdates() {
        String data;
        DatagramPacket packet;

        for (Player p : this.manager.getPlayers()) {
            if (!p.isHost()) {

                for (Robot r : this.game.getRobots()) {
                    data = Protocol.encodeMove(r, this.game.getCurrentRound());

                    packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);

                    try {
                        this.socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.sendRefresh(p.getIp());
                }
            }
        }
    }

    /***
     * Send a new proposition from a player, update the number in the list of
     * players
     *
     * @param user
     *            : user which made the proposition
     * @param nb
     *            : it proposition
     */
    public void updatePropositionfromPlayer(String user, int nb) {
        Player playerTmp = null;

        playerTmp = this.manager.getPlayerByPseudo(user);
        this.manager.sortPropositions(playerTmp, nb);

        // Update the list of players (left column on the screen) in everyone
        this.updatePlayers();

        if (!this.startCount && nb > 0) {
            this.startCount = true;
            this.count.startCountdown();
        }
    }

    /**
     * Start to play
     */
    public void startPlay() {
        this.count.stopCountdown();
        this.startCount = false;
        this.count.resetCountdown();
        this.setButtonValidate(false);
        this.manager.chooseHand();
        this.manager.getPlayers()[this.manager.getCurrentHand()].setHand(true);
        this.bestProposition = this.manager.getPlayers()[this.manager.getCurrentHand()].getNbMoveProposed();
        this.sendbestProposition(this.bestProposition);

        if (!this.manager.getPlayers()[this.manager.getCurrentHand()].isHost()) {
            this.sendHands(this.manager.getPlayers()[this.manager.getCurrentHand()].getIp(), true);
        } else {
            this.hand = true;
            this.controller.setEnabledforfeit(true);
        }
        this.sendMessage(this.manager.getPlayers()[this.manager.getCurrentHand()].getPseudo() + " win the hand with "
                + this.manager.getPlayers()[this.manager.getCurrentHand()].getNbMoveProposed() + " movements proposed");
    }

    /**
     * Send to client his hand
     *
     * @param ip
     *            : InetAddress
     * @param bool
     *            : true if he has the hand else false
     */
    private void sendHands(InetAddress ip, boolean bool) {
        String data = Protocol.encodeHand(bool);

        DatagramPacket packet = Protocol.encodePacket(data, ip, Constant.CLIENT_PORT);

        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.sendRefresh(ip);
    }

    /**
     * Sends the current best proposition to all clients
     *
     * @param proposition
     *            : int
     */
    private void sendbestProposition(int proposition) {

        for (Player p : this.manager.getPlayers()) {
            if (!p.isHost()) {
                String data = Protocol.encodeProposition(null, proposition);

                DatagramPacket packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);

                try {
                    this.socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.sendRefresh(p.getIp());
            }
        }
    }

    /**
     * At the end of a round, reset all players'proposition at 0
     */
    public void resetPropositions() {
        int cpt = 0;
        this.setButtonValidate(true);
        while (cpt < this.manager.getPlayers().length) {
            Player p = this.manager.getPlayers()[cpt];
            cpt++;
            this.updatePropositionfromPlayer(p.getPseudo(), 0);

            if (p.isHand()) {
                p.setHand(false);

                if (p.isHost()) {
                    this.hand = false;
                    this.controller.setEnabledforfeit(false);
                } else {
                    this.sendHands(p.getIp(), false);
                }
            }
        }
    }

    /**
     * Send a message in the console of all clients
     *
     * @param msg
     *            : String, the message
     */
    private void sendMessage(String msg) {

        for (Player p : this.manager.getPlayers()) {

            if (!p.isHost()) {
                String data = Protocol.encodeMessage(msg);

                DatagramPacket packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);

                try {
                    this.socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(msg);
            }
        }

    }

    /**
     * If the counter reaches zero, the next player takes the hand. If there are
     * more players to pass, we pass to the next round.
     */
    public void nextPlayerProposition() {

        // Remove the previous Hand
        this.manager.getPlayers()[this.manager.getCurrentHand()].setHand(false);
        if (!this.manager.getPlayers()[this.manager.getCurrentHand()].isHost()) {
            this.sendHands(this.manager.getPlayers()[this.manager.getCurrentHand()].getIp(), false);
        } else {
            this.hand = false;
            this.controller.setEnabledforfeit(false);
            this.game.setmNextPosition(new Stack<List<Robot>>());
            this.game.setmPreviousPosition(new Stack<List<Robot>>());
        }

        // Give the new Hand
        this.manager.setCurrentHand(this.manager.getCurrentHand() + 1);

        if (this.manager.getCurrentHand() < this.manager.getPlayers().length) {
            // replace the robots in the correct starting position
            this.replaceRobotAtStart();

            this.manager.getPlayers()[this.manager.getCurrentHand()].setHand(true);
            this.bestProposition = this.manager.getPlayers()[this.manager.getCurrentHand()].getNbMoveProposed();
            this.sendbestProposition(this.bestProposition);

            if (!this.manager.getPlayers()[this.manager.getCurrentHand()].isHost()) {
                this.sendHands(this.manager.getPlayers()[this.manager.getCurrentHand()].getIp(), true);
            } else {
                this.hand = true;
                this.controller.setEnabledforfeit(true);
            }
        }
        // If there everybody try it solution
        else {
            // if the goal is reached
            if (this.game.isWin(this.game.getRobot(this.game.getCurrentGoal().getColor()))) {
                this.game.startNewLap();
                Protocol.encodeGoalCard(this.game.getmGoalCards(), this.game.getCurrentGoal());
                this.round = this.game.getCurrentRound();
            } else {
                this.replaceRobotAtStart();
            }
            this.manager.setCurrentHand(-1);
            this.resetPropositions();
            this.bestProposition = 0;
            this.sendbestProposition(this.bestProposition);
        }

        this.controller.refreshBoard();
        this.controller.refreshColumn();
    }

    /**
     * Replace robot at start position, if a new player takes the hand
     */
    public void replaceRobotAtStart() {
        int x, y, cpt = 0;

        while (cpt < this.game.getRobots().size()) {
            x = this.game.getRobots().get(cpt).getOriginX();
            y = this.game.getRobots().get(cpt).getOriginY();
            this.game.getRobots().get(cpt).setX(x);
            this.game.getRobots().get(cpt).setY(y);
            cpt++;
        }
        this.sendUpdates();
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isHand() {
        return this.hand;
    }

    public void setHand(boolean hand) {
        this.hand = hand;
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

    public boolean isStartCount() {
        return this.startCount;
    }

    public void setStartCount(boolean startCount) {
        this.startCount = startCount;
    }

    public boolean isButtonValidate() {
        return this.buttonValidate;
    }

    public void setButtonValidate(boolean buttonValidate) {
        this.buttonValidate = buttonValidate;
        this.controller.setEnabledValidate(buttonValidate);

        for (Player p : this.manager.getPlayers()) {
            if (!p.isHost()) {
                String data = Protocol.encodeButtonValidate(buttonValidate);

                DatagramPacket packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);

                try {
                    this.socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendServerDisconnect() {
        for (Player p : this.manager.getPlayers()) {
            if (!p.isHost()) {
                String data = Protocol.encodeServerISDisconnect();

                DatagramPacket packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);

                try {
                    this.socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PlayerManager getManager() {
        return this.manager;
    }

    public void setManager(PlayerManager manager) {
        this.manager = manager;
    }
}
