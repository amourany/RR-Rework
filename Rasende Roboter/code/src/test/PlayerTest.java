package test;

import org.junit.Test;

import game.Player;
import junit.framework.TestCase;

public class PlayerTest extends TestCase {

    @Test
    public void testPlayer() {

        Player p;
        p = new Player(null, "test");
        assertEquals(false, p.isHost());
        p.setHost(true);
        assertEquals(true, p.isHost());
        assertEquals("test", p.getPseudo());
        p.setPseudo("new");
        assertEquals("new", p.getPseudo());
        assertEquals(0, p.getPoints());
        assertEquals(0, p.getNbMoveProposed());
        p.setNbMoveProposed(18);
        assertEquals(18, p.getNbMoveProposed());
        p.setPoints(67);
        assertEquals(67, p.getPoints());
    }
}
