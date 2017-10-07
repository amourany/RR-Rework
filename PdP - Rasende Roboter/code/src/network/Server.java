package network;

import game.BoardPiece;
import game.Constant;
import game.Game;
import game.Player;
import game.Robot;
import game.Constant.Color;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import controller.Controller;


/**
 * Server side of the application
 */
public class Server {
	
	private String username;
	private boolean hand;
	private int round;
	private int bestProposition;
	private boolean run;
	private boolean startCount;
	private boolean buttonValidate;
	private DatagramSocket socket;
	
	/**
	 * @see Game
	 */
	private Game game;
	/**
	 * @see PlayerManager
	 */
	private PlayerManager manager;
	/**
	 * @see Countdowwn
	 */
	private Countdown count; 
	/**
	 * @see Controller
	 */
	private Controller controller;

	private Thread serverThread = new Thread() {
		public void run() {
				listen();
		}
	};

	/**
	 * Constructor of Server
	 * @param controller
	 * @param pseudo : username that hosts the server
	 * @see Controller
	 */
	public Server(Controller controller,String pseudo)
	{
		run=true;
		count = new Countdown(controller);
		game = new Game(controller);
		hand=false;
		startCount=false;
		buttonValidate=true;
		controller.setEnabledforfeit(false);
		round=1;
		bestProposition=0;
		manager = new PlayerManager();
		username = pseudo;
		this.controller = controller;
		
		try {
			socket = new DatagramSocket(Constant.SERVER_PORT);
			Player p=new Player(socket.getInetAddress(),username);
			p.setHost(true);
			manager.addPlayer(p);
			sendMessage("The server is launched! ");
			this.controller.refreshPlayers(p.getPseudo()+"("+p.getPoints()+"pts) : "+p.getNbMoveProposed()+" movements proposed\n");
			this.controller.refreshColumn();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		serverThread.start();		
	}
	
	/**
	 * Stop the thread of the server
	 */
	public void stopServer() {
		count.stopCountdown();
		run=false;		
		socket.close();
	}
	
	/**
	 * Listen for the data from clients
	 */
	private void listen() {
		String[] data;
		boolean newplayer=true;
		try {
			byte[] buffer = new byte[1500];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			while(run) {
				socket.receive(packet);
				
				data = Protocol.decodePacket(packet);
				
					if(data[0].equals(Protocol.CONNECT)){
						
						//check if client username is free
						for(Player p : manager.getPlayers())
						{
							if(data[1].equals(p.getPseudo()))
							{
								newplayer=false;
								sendOtherUserName(packet.getAddress(),data[1]);
							}
						}	
						
						if(newplayer)
						{
							manager.addPlayer(new Player(packet.getAddress(), data[1]));
							sendBoard(packet.getAddress());
							sendRobots(packet.getAddress());
							sendCard(packet.getAddress());
							sendTime(packet.getAddress());
							updatePlayers();	
							sendRefresh(packet.getAddress());
						}
						else
							newplayer=true;
					}
					processData(data);
				
				}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the data sent by clients
	 * @param data : String
	 */
	private void processData(String[] data) {
		Color c;
		int roundTmp=1;
		
		switch(data[0]) {
		case Protocol.MOVE:
			
			int x = Integer.parseInt(data[1]);
			int y = Integer.parseInt(data[2]);
			c = Color.valueOf(data[3]);
			round = Integer.parseInt(data[4]);
			
			roundTmp = game.getCurrentRound();
			
			game.getRobot(c).setX(x);
			game.getRobot(c).setY(y);
			
			if (game.isWin(game.getRobot(c))) {
				game.startNewLap();
			}
			
			if(round!=roundTmp)
			{	
				addPoint();
				Protocol.encodeGoalCard(game.getmGoalCards(), game.getCurrentGoal());
				round=game.getCurrentRound();
				resetPropositions();
			}

			controller.refreshBoard();
			controller.refreshColumn();
			sendUpdates();
			break;
			
			case Protocol.PROPOSITION :
				updatePropositionfromPlayer(data[1], Integer.parseInt(data[2]));
				break;
			case Protocol.NEXTPROPOSITION :
				nextPlayerProposition();
				break;
				
			default:
				break;
		}
		
	}
	
	/**
	 * Updates the list of players for the server and all clients
	 */
	private void updatePlayers() {
		
		String data = "";
		DatagramPacket packet;
		
		//Update
		for(Player p2 : manager.getPlayers()) {
			data = Protocol.encodeClient(Arrays.asList(manager.getPlayers()));
			//If client
			if (!p2.isHost())
			{
				packet = Protocol.encodePacket(data, p2.getIp(), Constant.CLIENT_PORT);
			
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
			//If Server
			else if(p2.isHost())
			{
				data = data.substring(Protocol.CLIENT.length()+1); //substract "client&"
				controller.refreshPlayers(data);
			}
		}	
	}

	/**
	 * Add one point to the player who has the hand
	 */
	public void addPoint() {
		for(Player p : manager.getPlayers())
		{
			if(p.isHand())
			{
				p.setPoints(p.getPoints()+1);
				sendMessage(p.getPseudo()+" win the round (+1pts)");
			}
		}
		
	}
	
	/**
	 * Send the new board to all clients
	 */
	public void sendBoardToAllClients()
	{
		for(Player p: manager.getPlayers())
		{
			if(!p.isHost())
			{
				sendBoard(p.getIp());
				sendRobots(p.getIp());
				sendCard(p.getIp());
				sendTime(p.getIp());
				sendRefresh(p.getIp());
			}
		}
	}

	/**
	 * Send the new board to one client
	 * @param ip : InetAddress
	 */
	private void sendBoard(InetAddress ip) {
		String boardInfo;
		DatagramPacket packet;
		try {	
			for(BoardPiece bp : game.getBoard().getBoardPiece()) {
				boardInfo = Protocol.encodeBoardPiece(bp);
				
				packet = Protocol.encodePacket(boardInfo, ip, Constant.CLIENT_PORT);
				socket.send(packet);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param ip : InetAddress
	 */
	private void sendOtherUserName(InetAddress ip, String username) {
		String Info;
		DatagramPacket packet;
		try {	
			Info = Protocol.encodeOtherUserName(username);
			
			packet = Protocol.encodePacket(Info, ip, Constant.CLIENT_PORT);
			socket.send(packet);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send cards and goalcard to client
	 * @param ip : InetAddress
	 */
	private void sendCard(InetAddress ip) {
		String cardInfo = Protocol.encodeGoalCard(game.getmGoalCards(), game.getCurrentGoal());
		DatagramPacket packet = Protocol.encodePacket(cardInfo, ip, Constant.CLIENT_PORT);
	
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Send the starting position of all the robots
	 * @param ip : InetAddress
	 */
	private void sendRobots(InetAddress ip) {
		DatagramPacket packet;
		String robotInfo;
		try {		
			for(Robot r : game.getRobots()) {
				robotInfo = Protocol.encodeRobot(r);
				
				packet = Protocol.encodePacket(robotInfo, ip, Constant.CLIENT_PORT);
				
				socket.send(packet);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send the current time of the countdown to one client
	 * @param ip : InetAddress
	 */
	public void sendTime(InetAddress ip) {
		try {	
				String timeInfo = Protocol.encodeTime(count);
				
				DatagramPacket packet = Protocol.encodePacket(timeInfo, ip, Constant.CLIENT_PORT);
				
				socket.send(packet);

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send the current time of the countdown to all clients
	 */
	public void sendTimeToAllClients() {
		for(Player p : manager.getPlayers())
		{
			if(!p.isHost())
				sendTime(p.getIp());
		}
	}

	/**
	 * Ask all clients to refresh their screen
	 * @param ip : InetAddress
	 */
	private void sendRefresh(InetAddress ip) {
		String data = Protocol.REFRESH;
		DatagramPacket packet = Protocol.encodePacket(data, ip, Constant.CLIENT_PORT);
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send Updates to all clients when there is a movement
	 */
	public void sendUpdates() {
		String data;
		DatagramPacket packet;
		
		for(Player p : manager.getPlayers()) { 	
			if(!p.isHost())
			{
				
				for(Robot r : game.getRobots())
				{
					data = Protocol.encodeMove(r,game.getCurrentRound());
					
					packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);
					
					try {
						socket.send(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}		
						sendRefresh(p.getIp());
				}
			}
		}
	}
	
	/***
	 * Send a new proposition from a player, update the number in the list of players
	 * @param user : user which made the proposition
	 * @param nb : it proposition
	 */
	public void updatePropositionfromPlayer(String user, int nb)
	{
		Player playerTmp=null;
		
		playerTmp = manager.getPlayerByPseudo(user);
		manager.sortPropositions(playerTmp, nb);

		//Update the list of players (left column on the screen) in everyone
		updatePlayers();
		
		if(!startCount && nb>0)
		{
			startCount=true;
			count.startCountdown();
		}
	}
	
	/**
	 * Start to play
	 */
	public void startPlay(){
			count.stopCountdown();
			startCount=false;
			count.resetCountdown();
			setButtonValidate(false);
			manager.chooseHand();
			manager.getPlayers()[manager.getCurrentHand()].setHand(true);
			bestProposition = manager.getPlayers()[manager.getCurrentHand()].getNbMoveProposed();
			sendbestProposition(bestProposition);
			
			if(!manager.getPlayers()[manager.getCurrentHand()].isHost())
			{
				sendHands(manager.getPlayers()[manager.getCurrentHand()].getIp(),true);
			}
			else
			{
				hand=true;
				controller.setEnabledforfeit(true);
			}				
			sendMessage(manager.getPlayers()[manager.getCurrentHand()].getPseudo()+" win the hand with "+manager.getPlayers()[manager.getCurrentHand()].getNbMoveProposed()+" movements proposed");
	}
	
	/**
	 * Send to client his hand
	 * @param ip : InetAddress
	 * @param bool : true if he has the hand else false
	 */
	private void sendHands(InetAddress ip, boolean bool) {
		String data = Protocol.encodeHand(bool);
					
		DatagramPacket packet = Protocol.encodePacket(data, ip, Constant.CLIENT_PORT);
					
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		sendRefresh(ip);
	}
	
	/**
	 * Sends the current best proposition to all clients
	 * @param proposition : int
	 */
	private void sendbestProposition(int proposition) {
		
		for (Player p : manager.getPlayers())
		{
			if(!p.isHost())
			{
				String data = Protocol.encodeProposition(null,proposition);
				
				DatagramPacket packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);
							
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}		
				
				sendRefresh(p.getIp());
			}
		}
	}
	
	/**
	 * At the end of a round, reset all players'proposition at 0
	 */
	public void resetPropositions() {
		int cpt=0;
		setButtonValidate(true);
		while(cpt<manager.getPlayers().length)
		{
			Player p = manager.getPlayers()[cpt];
			cpt++;
			updatePropositionfromPlayer(p.getPseudo(), 0);
			
			if(p.isHand())
			{
				p.setHand(false);
				
				if(p.isHost())
				{
					hand=false;
					controller.setEnabledforfeit(false);
				}
				else
					sendHands(p.getIp(), false);							
			}
		}
	}
	
	/**
	 * Send a message in the console of all clients
	 * @param msg : String, the message
	 */
	private void sendMessage(String msg) {
		
		for(Player p : manager.getPlayers())
		{
		
			if(!p.isHost())
			{
				String data = Protocol.encodeMessage(msg);
				
				DatagramPacket packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);
							
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
			else
			{
				System.out.println(msg);
			}
		}
		
	}

	
	/**
	 * If the counter reaches zero, the next player takes the hand. 
	 * If there are more players to pass, we pass to the next round.
	 */
	public void nextPlayerProposition() {
		
		//Remove the previous Hand
		manager.getPlayers()[manager.getCurrentHand()].setHand(false);
		if(!manager.getPlayers()[manager.getCurrentHand()].isHost())
		{
			sendHands(manager.getPlayers()[manager.getCurrentHand()].getIp(),false);
		}
		else
		{
			hand=false;
			controller.setEnabledforfeit(false);
			game.setmNextPosition(new Stack<List<Robot>>());
			game.setmPreviousPosition(new Stack<List<Robot>>());
		}
			
		//Give the new Hand
		manager.setCurrentHand(manager.getCurrentHand()+1);
		
		if(manager.getCurrentHand()<manager.getPlayers().length)
		{
			//replace the robots in the correct starting position
			replaceRobotAtStart();
			
			manager.getPlayers()[manager.getCurrentHand()].setHand(true);
			bestProposition=manager.getPlayers()[manager.getCurrentHand()].getNbMoveProposed();
			sendbestProposition(bestProposition);
			
			if(!manager.getPlayers()[manager.getCurrentHand()].isHost())
				sendHands(manager.getPlayers()[manager.getCurrentHand()].getIp(),true);
			else
			{
				hand=true;	
				controller.setEnabledforfeit(true);
			}
		}
		//If there everybody try it solution
		else
		{
			//if the goal is reached
			if(game.isWin(game.getRobot(game.getCurrentGoal().getColor())))
			{
				game.startNewLap();
				Protocol.encodeGoalCard(game.getmGoalCards(),game.getCurrentGoal());
				round=game.getCurrentRound();
			}
			else
			{
				replaceRobotAtStart();
			}
			manager.setCurrentHand(-1);
			resetPropositions();
			bestProposition=0;
			sendbestProposition(bestProposition);
		}
		
		controller.refreshBoard();
		controller.refreshColumn();
	}
	
	/**
	 * Replace robot at start position, if a new player takes the hand
	 */
	public void replaceRobotAtStart()
	{
		int x,y,cpt=0;
		
		while(cpt < game.getRobots().size())
		{
			x=game.getRobots().get(cpt).getOriginX();
			y=game.getRobots().get(cpt).getOriginY();
			game.getRobots().get(cpt).setX(x);
			game.getRobots().get(cpt).setY(y);				
			cpt++;
		}
		sendUpdates();
	}
	
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}
	
	public String getUsername() {
		return username;
	}

	public boolean isHand() {
		return hand;
	}

	public void setHand(boolean hand) {
		this.hand = hand;
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
	
	public boolean isStartCount() {
		return startCount;
	}

	public void setStartCount(boolean startCount) {
		this.startCount = startCount;
	}

	public boolean isButtonValidate() {
		return buttonValidate;
	}

	public void setButtonValidate(boolean buttonValidate) {
		this.buttonValidate = buttonValidate;
		controller.setEnabledValidate(buttonValidate);
		
		for (Player p : manager.getPlayers())
		{
			if(!p.isHost())
			{
				String data = Protocol.encodeButtonValidate(buttonValidate);
				
				DatagramPacket packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);
							
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}
	}

	public void sendServerDisconnect() {
		for (Player p : manager.getPlayers())
		{
			if(!p.isHost())
			{
				String data = Protocol.encodeServerISDisconnect();
				
				DatagramPacket packet = Protocol.encodePacket(data, p.getIp(), Constant.CLIENT_PORT);
							
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}
	}
	
	public PlayerManager getManager() {
		return manager;
	}

	public void setManager(PlayerManager manager) {
		this.manager = manager;
	}
}