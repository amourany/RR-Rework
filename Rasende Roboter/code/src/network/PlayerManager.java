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

    public PlayerManager() {
        this.players = new Player[0];
        this.currentHand = -1;
    }

    public void addPlayer(Player p) {
        this.players = Arrays.copyOf(this.players, this.players.length + 1);
        this.players[this.players.length - 1] = p;
    }

    public void chooseHand() {
        boolean found = false;
        for (int i = 0; i < this.players.length; i++) {
            if (!found) {
                if (this.players[i].getNbMoveProposed() > 0) {
                    this.currentHand = i;
                    found = true;
                }
            }
        }
    }

    public void sortPropositions(Player p, int proposition) {
        Player tmpPlayers;

        for (Player p2 : this.players) {
            if (p2.getPseudo().equals(p.getPseudo())) {
                p2.setNbMoveProposed(proposition);
            }
        }

        for (int i = 0; i < this.players.length - 1; i++) {
            if (this.players[i].getNbMoveProposed() >= this.players[i + 1].getNbMoveProposed()) {
                tmpPlayers = this.players[i + 1];
                this.players[i + 1] = this.players[i];
                this.players[i] = tmpPlayers;
            }
        }
    }

    public void deletePlayer(Player p) {
        boolean found = false;
        Player[] tmp = Arrays.copyOf(this.players, this.players.length);

        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i].getPseudo() == p.getPseudo()) {
                found = true;
            } else {
                if (!found) {
                    tmp[i] = this.players[i];
                } else {
                    tmp[i - 1] = this.players[i];
                }
            }
        }
        this.players = Arrays.copyOf(tmp, tmp.length - 1);
    }

    public Player getPlayerByPseudo(String user) {
        Player result = null;

        for (Player p : this.players) {
            if (p.getPseudo().equals(user)) {
                result = p;
            }
        }

        return result;
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int getCurrentHand() {
        return this.currentHand;
    }

    public void setCurrentHand(int currentHand) {
        this.currentHand = currentHand;
    }

    public boolean doesEveryoneProposed() {
        boolean res = true;

        for (Player p : this.players) {
            if (p.getNbMoveProposed() == 0) {
                res = false;
            }
        }
        return res;
    }
}
