package fr.amou.perso.app.rasen.robot.network;

import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;
import java.util.Stack;

import fr.amou.perso.app.rasen.robot.game.BoardPiece;
import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Constant;
import fr.amou.perso.app.rasen.robot.game.Player;
import fr.amou.perso.app.rasen.robot.game.Robot;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;

/**
 * Network protocol
 */
public class Protocol {

    public static final String ROBOT = "robot";
    public static final String BOARD_PIECE = "board_piece";
    public static final String GOAL_CARDS = "goalCards";
    public static final String TIME = "time";
    public final static String MOVE = "move";
    public final static String CLIENT = "client";
    public final static String REFRESH = "refresh";
    public final static String CONNECT = "connect";
    public final static String PROPOSITION = "proposition";
    public final static String HAND = "hand";
    public static final String MESSAGE = "message";
    public static final String NEXTPROPOSITION = "nextProposition";
    public static final String OTHER_USERNAME = "otherUsername";
    public static final String BUTTON_VALIDATE = "buttonValidate";
    public static final String SERVER_DISCONNECT = "serverDisconnect";

    public static DatagramPacket encodePacket(String msg, InetAddress ip, int port) {
        byte[] msgBytes = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, ip, port);
        packet.setData(msgBytes);
        return packet;
    }

    public static String[] decodePacket(DatagramPacket packet) {
        String data = new String(packet.getData(), 0, packet.getLength());
        String[] splitData = data.split("&");
        return splitData;
    }

    public static String encodeConnect(String username) {
        String msg = CONNECT + "&" + username;
        return msg;
    }

    public static String encodeRobot(Robot r) {
        String robotInfo = ROBOT + "&" + r.x + "&" + r.y + "&" + r.getColor() + "&" + r.originX + "&" + r.originY;
        return robotInfo;
    }

    public static Robot decodeRobot(String[] msg) {

        int x = Integer.parseInt(msg[1]);
        int y = Integer.parseInt(msg[2]);
        int originX = Integer.parseInt(msg[4]);
        int originY = Integer.parseInt(msg[5]);
        return new Robot(x, y, Color.valueOf(msg[3]), originX, originY);
    }

    public static String encodeBoardPiece(BoardPiece bp) {
        String boardInfo = BOARD_PIECE + "&" + bp.getInitialLocation() + "&" + bp.getFinalLocation() + "&"
                + bp.getXmlFile();
        return boardInfo;
    }

    public static BoardPiece decodeBoardPiece(String[] msg) {
        int initialLocation = Integer.parseInt(msg[1]);
        int finalLocation = Integer.parseInt(msg[2]);
        return new BoardPiece(new File(msg[3]), initialLocation, finalLocation);
    }

    public static String encodeGoalCard(Stack<Box> goal, Box currentGoal) {
        String s = GOAL_CARDS;

        for (Box b : goal) {
            s += "&" + b.getType() + ";" + b.getColor();
        }

        s += "&" + currentGoal.getType() + ";" + currentGoal.getColor();
        return s;
    }

    public static Stack<Box> decodeGoalCard(String[] msg) {
        Stack<Box> goal = new Stack<>();
        String info[];

        for (String card : msg) {
            if (!card.equals(GOAL_CARDS)) {
                info = card.split(";");
                if (info[1].equals("null")) {
                    goal.push(new Box(Constant.BoxType.valueOf(info[0]), null));
                } else {
                    goal.push(new Box(Constant.BoxType.valueOf(info[0]), Constant.Color.valueOf(info[1])));
                }
            }
        }
        return goal;
    }

    public static String encodeTime(Countdown count) {
        String msg = TIME + "&" + count.getTime();
        return msg;
    }

    public static int decodeTime(String[] msg) {
        int time = Integer.parseInt(msg[1]);
        return time;
    }

    public static String encodeProposition(String username, int nb) {
        String msg = PROPOSITION + "&" + username + "&" + nb;
        return msg;
    }

    public static String encodeHand(boolean bool) {
        String msg = HAND + "&" + bool;
        return msg;
    }

    public static boolean decodeHand(String[] msg) {
        boolean hand = Boolean.parseBoolean(msg[1]);
        return hand;
    }

    public static String encodeClient(List<Player> players) {
        String clientInfo = CLIENT + "&";
        for (Player p : players) {
            clientInfo += p.getPseudo() + " (" + p.getPoints() + " pts) - " + p.getNbMoveProposed()
                    + "movements proposed\n";
        }
        return clientInfo;
    }

    public static String decodeClient(String[] msg) {
        return msg[1];
    }

    public static String encodeMove(Robot r, int round) {
        String moveInfo = MOVE + "&" + r.x + "&" + r.y + "&" + r.getColor() + "&" + round;
        return moveInfo;
    }

    public static String encodeAskNextProposition() {
        String emptyInfo = NEXTPROPOSITION + "&";
        return emptyInfo;
    }

    public static String encodeMessage(String msg) {
        String Info = MESSAGE + "&" + msg;
        return Info;
    }

    public static String encodeOtherUserName(String username) {
        String Info = OTHER_USERNAME + "&" + username;
        return Info;
    }

    public static String encodeButtonValidate(boolean buttonValidate) {
        String Info = BUTTON_VALIDATE + "&" + buttonValidate;
        return Info;
    }

    public static String encodeServerISDisconnect() {
        String Info = SERVER_DISCONNECT + "&";
        return Info;
    }
}