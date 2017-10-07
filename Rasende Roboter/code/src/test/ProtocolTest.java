package test;

import org.junit.Test;

import game.Constant.Color;
import game.Robot;
import junit.framework.TestCase;
import network.Protocol;

public class ProtocolTest extends TestCase {

    @Test
    public void testEncode() {
        assertEquals("connect&toto", Protocol.encodeConnect("toto"));

        Robot r = new Robot(1, 2, Color.Green, 3, 4);
        assertEquals("robot&1&2&Green&3&4", Protocol.encodeRobot(r));

        assertEquals("proposition&toto&10", Protocol.encodeProposition("toto", 10));

        assertEquals("hand&true", Protocol.encodeHand(true));

        assertEquals("move&1&2&Green&10", Protocol.encodeMove(r, 10));
    }

    @Test
    public void testDecode() {
        String[] msg = { "robot", "1", "2", "Green", "3", "4" };
        Robot r = new Robot(1, 2, Color.Green, 3, 4);

        assertTrue(r.equals(Protocol.decodeRobot(msg)));
    }
}
