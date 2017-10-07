package fr.amou.perso.app.rasen.robot.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import fr.amou.perso.app.rasen.robot.controller.Controller;
import fr.amou.perso.app.rasen.robot.network.Client;
import fr.amou.perso.app.rasen.robot.network.Server;
import junit.framework.TestCase;

public class NetworkTest extends TestCase {

    @Test
    public void testNetwork() {
        Controller c1 = new Controller();
        Controller c2 = new Controller();

        Server server = new Server(c1, "ServerName");

        assertEquals(server.getUsername(), "ServerName");
        assertEquals(server.getBestProposition(), 0);

        try {
            Client client = new Client(InetAddress.getByName("127.0.0.1"), c2, "ClientName");

            assertEquals(client.getUsername(), "ClientName");
            assertEquals(client.getBestProposition(), 0);

        } catch (UnknownHostException e) {
            System.out.println("Bad Ip Adress");
        }

    }
}
