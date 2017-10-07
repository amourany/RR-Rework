package test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import controller.Controller;
import game.Constant.Color;
import game.Robot;
import junit.framework.TestCase;
import network.Client;
import network.Protocol;

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
