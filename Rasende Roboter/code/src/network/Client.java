package network;

import game.Constant;
import game.Constant.Color;
import game.Robot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Stack;

import controller.Controller;


/**
 * Client side of the application
 */
public class Client{
	
	private InetAddress ipServer;
	DatagramSocket socket;
	private String username;
	private boolean hand;
	private int bestProposition;
	private boolean run;
	
	/**
	 * @see Controller
	 */
	private Controller controller;
	/**
	 * @see Countdown
	 */
	private Countdown count;

	private Thread clientThread = new Thread() {
	
		
		public void run() {
			listen();
		}
	};
	
	/**
	 * Constructor of Client
	 * @param dest : InetAddress, server IP adress
	 * @param controller
	 * @param pseudo : username of the client
	 * @see Controller
	 */
	public Client(InetAddress dest, Controller controller,String pseudo) {
		count=new Countdown(controller);
		run=true;
		ipServer = dest;
		hand = false;
		controller.setEnabledforfeit(false);
		setBestProposition(0);
		this.controller = controller;
		this.username=pseudo;
		
		try {
			socket = new DatagramSocket(Constant.CLIENT_PORT);
			count.stopCountdown();
			controller.setOnlinePerspective(true);
			clientThread.start();
			System.out.println("There is a server");
		} catch (SocketException e) {
			controller.setOnlinePerspective(false);
			System.out.println("There is no server at this IPaddress");
			//e.printStackTrace();
		}
	}
	
	/**
	 * Stop the thread of the client
	 */
	public void stopClient()
	{
		run=false;
		socket.close();
	}
	
	/**
	 * Listen for the data from the server
	 */
	private void listen() {
		String[] data;
		byte[] buffer = new byte[15000];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		while(run) {
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			data = Protocol.decodePacket(packet);
			
			processData(data);
			}
	}

	/**
	 * Connection with server
	 */
	public void connect() {
		DatagramPacket packet;
		
		try {
			String data = Protocol.encodeConnect(username);
			packet = Protocol.encodePacket(data, ipServer, Constant.SERVER_PORT);
			
			socket.send(packet);			
		} catch (SocketException e) {
			System.out.println("SocketException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");			
			e.printStackTrace();
		}
	}
	
	/**
	 * Processes the data sent by server
	 * @param data : String
	 */
	public void processData(String[] data) {
		
		switch(data[0]) {
			case Protocol.GOAL_CARDS:
				controller.setCurrentGoal(Protocol.decodeGoalCard(data));
				break;
			case Protocol.BOARD_PIECE:
				controller.setBoard(Protocol.decodeBoardPiece(data));
				break;
			case Protocol.ROBOT:
				controller.setRobot(Protocol.decodeRobot(data));
				break;
			case Protocol.TIME:
				count.setTime(Protocol.decodeTime(data));
				controller.refreshColumn();
				break;
			case Protocol.MOVE:
				int x = Integer.parseInt(data[1]);
				int y = Integer.parseInt(data[2]);
				Color c = Color.valueOf(data[3]);
				
				controller.getgame().getRobot(c).setX(x);
				controller.getgame().getRobot(c).setY(y);
				
				if (controller.getgame().isWin(controller.getgame().getRobot(c))) {
					controller.getgame().startNewLap();
				}
				
				break;
			case Protocol.CLIENT:
				controller.refreshPlayers(Protocol.decodeClient(data));
				break;
			case Protocol.HAND:
				setHand(Protocol.decodeHand(data));
				break;
			case Protocol.PROPOSITION:
				bestProposition=Integer.parseInt(data[2]);
				break;
			case Protocol.REFRESH:
				controller.refreshBoard();
				controller.refreshColumn();
				break;
			case Protocol.MESSAGE:
				System.out.println(data[1]);
				break;
			case Protocol.OTHER_USERNAME:
				controller.stopClient(data[1]+" is not a free username, please choose an other");
				break;
			case Protocol.BUTTON_VALIDATE:
				System.out.println(data[1]);
				controller.setEnabledValidate(Boolean.valueOf(data[1]));
				break;
			case Protocol.SERVER_DISCONNECT:
				controller.stopClient("The server is offline");
				break;
			default:
				System.out.println("Flag inconnu :"+data[0]);
				break;
				
				
		}
	}	
	
	/**
	 * After a movement, send new positions of robots and update other data
	 * @param newRound : the current round
	 * @param nbMove : the number of movements
	 */
	public void sendMove(int newRound) {
		DatagramPacket packet;
		
			try {	
				
				for(Robot r : controller.getgame().getRobots())
				{
					String robotInfo = Protocol.encodeMove(r,newRound);				
					
					packet = Protocol.encodePacket(robotInfo, ipServer, Constant.SERVER_PORT);
					socket.send(packet);		
				}

			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Send a proposition of movement's number to server
	 * @param username : String, his username
	 * @param i : Integer, his proposition
	 */
	public void sendPropositionfromPlayer(String username, Integer i) {
		String proposition=Protocol.encodeProposition(username, i);
		DatagramPacket packet = Protocol.encodePacket(proposition, ipServer, Constant.SERVER_PORT);
	
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Asks the server to the next player
	 */
	public void askNextProposition() {
		controller.getgame().setmNextPosition(new Stack<List<Robot>>());
		controller.getgame().setmPreviousPosition(new Stack<List<Robot>>());
		
		String proposition=Protocol.encodeAskNextProposition();
		DatagramPacket packet = Protocol.encodePacket(proposition, ipServer, Constant.SERVER_PORT);
	
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getUsername() {
		return username;
	}

	public boolean isHand() {
		return hand;
	}

	public void setHand(boolean hand) {
		this.hand = hand;
		controller.setEnabledforfeit(hand);
	}

	public int getBestProposition() {
		return bestProposition;
	}

	public void setBestProposition(int bestProposition) {
		this.bestProposition = bestProposition;
	}
	
	public Countdown getCount() {
		return count;
	}

	public void setCount(Countdown count) {
		this.count = count;
	}
	
	public Boolean isRunning(){
		return run;
	}
}