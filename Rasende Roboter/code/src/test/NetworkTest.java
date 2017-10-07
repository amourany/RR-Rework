package test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import junit.framework.TestCase;
import network.Client;
import network.Server;

import org.junit.Test;

import controller.Controller;

public class NetworkTest extends TestCase{
	
	@Test
	public void testNetwork() {
		Controller c1 = new Controller();
		Controller c2 = new Controller();
		
		Server server = new Server(c1,"ServerName");

		assertEquals(server.getUsername(),"ServerName");
		assertEquals(server.getBestProposition(),0);
		
		try {
			Client client = new Client(InetAddress.getByName("127.0.0.1"),c2,"ClientName");
			
			assertEquals(client.getUsername(),"ClientName");
			assertEquals(client.getBestProposition(),0);			
			
		} catch (UnknownHostException e) {
			System.out.println("Bad Ip Adress");
		}
		
	}
}
