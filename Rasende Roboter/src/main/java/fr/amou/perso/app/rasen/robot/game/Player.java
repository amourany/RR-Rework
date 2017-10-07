package fr.amou.perso.app.rasen.robot.game;

import java.net.InetAddress;

import lombok.Data;

/**
 * Class containing the data of a user
 */
@Data
public class Player {
    private InetAddress ip;
    private String pseudo;
    private int points;
    private int nbMoveProposed;
    private boolean host;
    private boolean hand;

    public Player(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * Constructor
     *
     * @param ip
     *            : InetAddress
     * @param pseudo
     *            : String for the username
     */
    public Player(final InetAddress ip, final String pseudo) {
        this.ip = ip;
        this.pseudo = pseudo;
        this.points = 0;
        this.nbMoveProposed = 0;
        this.host = false;
    }

}
