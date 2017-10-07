package game;

import java.net.InetAddress;

/**
 * Class containing the data of a user
 */
public class Player {
	private InetAddress ip;
	private String pseudo;
	private int points;
	private int nbMoveProposed;
	private boolean host;
	private boolean hand;
	
	public Player(String pseudo)
	{
		this.pseudo = pseudo;
	}
	
	/**
	 * Constructor
	 * @param ip : InetAddress
	 * @param pseudo : String for the username
	 */
	public Player(final InetAddress ip, final String pseudo)
	{
		this.ip = ip;
		this.pseudo = pseudo;
		this.points=0;
		this.nbMoveProposed=0;
		this.host=false;
	}

	public boolean isHost() {
		return host;
	}

	public void setHost(final boolean host) {
		this.host = host;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(final InetAddress ip) {
		this.ip = ip;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(final String pseudo) {
		this.pseudo = pseudo;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(final int points) {
		this.points = points;
	}

	public int getNbMoveProposed() {
		return nbMoveProposed;
	}

	public void setNbMoveProposed(final int nbMove) {
		this.nbMoveProposed = nbMove;
	}

	public boolean isHand() {
		return hand;
	}

	public void setHand(boolean hand) {
		this.hand = hand;
	}
}
