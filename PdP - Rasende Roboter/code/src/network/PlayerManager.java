package network;

import java.util.Arrays;

import game.Player;

/**
 * Manages the order of the players in a network game
 */
public class PlayerManager {
	/**
	 * @see Player
	 */
	private Player[] players;
	private int currentHand;
	
	public PlayerManager(){
		players = new Player[0];
		currentHand = -1;
	}
	
	public void addPlayer(Player p) {
		players =  Arrays.copyOf(players, players.length+1);
		players[players.length-1] = p;
	}
	
	public void chooseHand(){
		boolean found= false;
		for(int i = 0 ; i < players.length; i++){
			if(!found) {
				if(players[i].getNbMoveProposed() > 0) {
					currentHand = i;
					found = true;
				}
			}
		}
	}
	
	public void sortPropositions(Player p, int proposition) {
		Player tmpPlayers;
		
		for(Player p2 : players) {
			if(p2.getPseudo().equals(p.getPseudo()))
					p2.setNbMoveProposed(proposition);
		}
		
		for(int i = 0; i < players.length-1; i++){
			if(players[i].getNbMoveProposed() >= players[i+1].getNbMoveProposed()){
				tmpPlayers = players[i+1];
				players[i+1] = players[i];
				players[i] = tmpPlayers;
			}
		}
	}

	public void deletePlayer(Player p) {
		boolean found = false;
		Player[] tmp = Arrays.copyOf(players, players.length);
		
		for(int i = 0; i < players.length; i++) {
			if(players[i].getPseudo() == p.getPseudo()) { found = true; }
			else {
				if(!found) { 
					tmp[i] = players[i]; 
				}
				else {
					tmp[i-1] = players[i];
				}
			}
		}
		players = Arrays.copyOf(tmp, tmp.length-1);
	}

	public Player getPlayerByPseudo(String user) {
		Player result = null;
		
		for(Player p : players) {
			if(p.getPseudo().equals(user)) {
				result = p;
			}
		}
		
		return result;
	}
	
	public Player[] getPlayers() {
		return players;
	}

	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public int getCurrentHand() {
		return currentHand;
	}

	public void setCurrentHand(int currentHand) {
		this.currentHand = currentHand;
	}

	public boolean doesEveryoneProposed() {
		boolean res = true;
		
		for(Player p : players) {
			if(p.getNbMoveProposed() == 0)
				res = false;
		}
		return res;
	}	
}
