package fr.amou.perso.app.rasen.robot.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import fr.amou.perso.app.rasen.robot.controller.Controller;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Robot;
import fr.amou.perso.app.rasen.robot.network.Client;
import fr.amou.perso.app.rasen.robot.network.Protocol;
import junit.framework.TestCase;

public class ClientTest extends TestCase {

    @Test
    public void testProcessData() {
        Controller controller = new Controller();
        Client client;
        try {
            client = new Client(InetAddress.getLocalHost(), controller, "toto");

            String[] data = { "robot", "1", "2", "Green", "3", "4" };
            Robot r = Protocol.decodeRobot(data);

            client.processData(data);
            assertTrue(r.equals(controller.getgame().getRobot(Color.Green)));

            data = new String[] { "hand", "true" };

            client.processData(data);
            assertTrue(client.isHand());

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
