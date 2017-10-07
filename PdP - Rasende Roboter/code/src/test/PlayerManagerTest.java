package test;

import game.Player;
import network.PlayerManager;

import org.junit.Test;

import junit.framework.TestCase;

public class PlayerManagerTest extends TestCase {

	@Test
	public void testPlayerManager() {
		PlayerManager pm = new PlayerManager();
		Player p = new Player("toto");
		
		pm.addPlayer(p);
		
		assertEquals(1, pm.getPlayers().length);
	}
	
	@Test
	public void testSortPropositions() {
		PlayerManager pm = new PlayerManager();
		Player p = new Player("toto");
		Player p2 = new Player("titi");
		
		pm.addPlayer(p);
		pm.addPlayer(p2);
		
		pm.sortPropositions(p, 5);
		
		assertEquals(0, pm.getPlayers()[0].getNbMoveProposed());
		assertEquals(5, pm.getPlayers()[1].getNbMoveProposed());		
		
		pm.sortPropositions(p2, 10);
		
		assertEquals(5, pm.getPlayers()[0].getNbMoveProposed());
		assertEquals(10, pm.getPlayers()[1].getNbMoveProposed());	
	}
	
	@Test
	public void testChooseHand(){
		PlayerManager pm = new PlayerManager();
		Player p = new Player("toto");
		Player p2 = new Player("titi");
		
		pm.addPlayer(p);
		pm.addPlayer(p2);
		
		assertEquals(-1, pm.getCurrentHand());
		
		pm.sortPropositions(p, 5);
		pm.chooseHand();
		
		assertEquals(1, pm.getCurrentHand());
		
		pm.sortPropositions(p2, 10);
		pm.chooseHand();
		
		assertEquals(0, pm.getCurrentHand());
	}
	
	@Test
	public void testDeletePlayer() {
		PlayerManager pm = new PlayerManager();
		Player p = new Player("toto");
		Player p2 = new Player("titi");
		
		pm.addPlayer(p);
		pm.addPlayer(p2);
		
		pm.deletePlayer(p);
		
		assertEquals(1, pm.getPlayers().length);
		assertEquals("titi", pm.getPlayers()[0].getPseudo());
	}
	
	@Test
	public void testDoesEveryoneProposed() {
		PlayerManager pm = new PlayerManager();
		Player p = new Player("toto");
		Player p2 = new Player("titi");

		pm.addPlayer(p);
		pm.addPlayer(p2);
		
		pm.sortPropositions(p, 5);
		
		assertEquals(false, pm.doesEveryoneProposed());
		
		pm.sortPropositions(p2, 10);
		
		assertEquals(true, pm.doesEveryoneProposed());
	}
}
