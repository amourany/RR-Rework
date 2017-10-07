package controller;

import game.BoardPiece;
import game.Box;
import game.Constant;
import game.Game;
import game.Robot;
import game.Constant.Color;
import game.Constant.Direction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Stack;

import network.Client;
import network.Countdown;
import network.Server;

import userInterface.RasendeFrame;
import userInterface.RasendeViewInterface;

public class Controller extends OutputStream implements ActionListener, MouseListener, KeyListener, WindowListener{
	public final static String ACTION_PREVIOUS = "ACTION_PREVIOUS"; 
	public final static String ACTION_NEXT = "ACTION_NEXT";
	public final static String ACTION_QUIT = "ACTION_QUIT";
	public final static String ACTION_NEW_GAME = "ACTION_NEW_GAME";
	public final static String ACTION_HELP = "ACTION_HELP";
	public final static String ACTION_LICENSE = "ACTION_LICENSE";
	public final static String ACTION_SOLVE = "ACTION_SOLVE";
	public final static String ACTION_START_SERVER = "ACTION_START_SERVER";
	public final static String ACTION_JOIN_SERVER = "ACTION_JOIN_SERVER";
	public final static String ACTION_VALIDATE = "ACTION_VALIDATE";
	public final static String ACTION_THEME_DEFAULT = "ACTION_THEME_DEFAULT";
	public final static String ACTION_THEME_POKEMON = "ACTION_THEME_POKEMON";
	public final static String ACTION_FORFEIT = "ACTION_FORFEIT";
	public final static String DISCONNECT = "DISCONNECT";
	
	private Game game;
	private final RasendeViewInterface frame;
	private Server server;
	private Client client;

	public Controller() {
		super();
		
		final PrintStream out = new PrintStream(this);
		System.setOut(out);

		game = new Game(this);		
		frame = new RasendeFrame(this);	
		frame.setOnlinePerspective(false);
	}		

	public Game getgame() {
		return game;
	}

	public void moveRobotInDirection(final Direction dir) {
		int x =-1 ,y = -1;

		if (game.getSelectedRobot() != null) {
			
			//if it is a network game and the player will reach its maximum number of allowed movements
			if((server!=null && game.getmPreviousPosition().size()+1==server.getBestProposition()+1)
					||(client!=null && game.getmPreviousPosition().size()+1==client.getBestProposition()+1))
			{
				moveLimit();
			}
			else
			{
	
				if(client!=null || server !=null) {
					x=game.getSelectedRobot().getX();
					y=game.getSelectedRobot().getY();
				}
	
				game.moveSelectedRobot(dir);
				
				refreshBoard();
				frame.displayDataInfo(game,this);
	
				//if client, send movement to server (only if there is a new location)
				if (client!=null && (game.getSelectedRobot().x != x || game.getSelectedRobot().y != y))
					client.sendMove(game.getCurrentRound());
				
				//if sever, send movement to other clients (only if there is new location)
				else if(server !=null && (game.getSelectedRobot().x != x || game.getSelectedRobot().y != y))
					server.sendUpdates();
	
				if(game.isOver()) {	gameOver(); }
			}
		}
	}

	private void askToQuit(){
		if(server!=null)
			server.getCount().stopCountdown();
		else if (client!=null)
			client.getCount().stopCountdown();
		frame.dispose();
		System.exit(0);
	}

	public void moveLimit() {
		frame.displayMoveLimit();
	}
	public void timeLimit() {		
		
		if(server!=null)
		{
			server.startPlay();
			//nextPlayerProposition();
		}
	}

	public void gameOver() {		
		frame.displayWin();
		game.startNewGame();
		frame.display(game,this);
	}
	
	public void stopClient(String msg)
	{
		client.stopClient();
		client=null;
		frame.setOnlinePerspective(false);
		System.out.println(msg);
	}

	public void actionPerformed(final ActionEvent e) {
		switch (e.getActionCommand()){
		case ACTION_PREVIOUS :
			game.loadPreviousPosition();
			frame.display(game,this);
			if(server!= null)
			{
				server.sendUpdates();
			}
			else if (client != null)
			{
				client.sendMove(game.getCurrentRound());
			}
			break;				
		case ACTION_NEXT :
			game.loadNextPosition();
			frame.display(game,this);
			if(server!= null)
			{
				server.sendUpdates();
			}
			else if (client != null)
			{
				client.sendMove(game.getCurrentRound());
			}
			break;
		case ACTION_QUIT :
			if(server!=null)
			{
				server.sendServerDisconnect();
			}
			askToQuit();
			break;
		case ACTION_NEW_GAME :
			if(client!=null)
			{
				stopClient("New Solo Game");
			}
			game.startNewGame();
			frame.display(game,this);
			if(server!=null)
			{
				server.sendBoardToAllClients();
			}
			break;
		case ACTION_HELP :
			frame.displayHelp();
			break;
		case ACTION_LICENSE :
			frame.displayLicense();
			break;
		case ACTION_SOLVE :
			game.startSolver();
			break;
		case ACTION_START_SERVER :
			if(client == null) {
				String user = frame.displayStartServer();
				if ((user != null) && (user.length() > 0)) {
					server = new Server(this,user);
					game = server.getGame();
					game.startNewGame();
					frame.setOnlinePerspective(true);
					frame.display(game,this);
				}
			}
			break;
		case ACTION_JOIN_SERVER :
			if(server == null) {
				String[] infos = frame.displayJoinServer();
				if (infos[0] != null){
					try {
						client = new Client(InetAddress.getByName(infos[1]), this, infos[0]);
						client.connect();
					} catch (UnknownHostException e1) {
						//e1.printStackTrace();
						System.out.println("Bad IPAdress, please retry");
					}	
				}	
			}
			break;
			
		case DISCONNECT :
			if(client != null)
			{
				stopClient("You are disconnected");
			}
			else if (server !=null)
			{
				server.sendServerDisconnect();
				server.stopServer();
				server=null;
				frame.setOnlinePerspective(false);
			}
			game.startNewGame();
			frame.display(game,this);
			break;
		case ACTION_VALIDATE :
			if((server != null || client != null)){				
				int suggestion = frame.getSuggestion();
				if(suggestion >= 0){
					if(server != null){
						server.updatePropositionfromPlayer(server.getUsername(), suggestion);
					}
					else if(client != null){
						client.sendPropositionfromPlayer(client.getUsername(), suggestion);
					}
				}					
			}
			break;
		case ACTION_FORFEIT:
			if(server != null)
			{
				server.nextPlayerProposition();
			}
			else if (client != null)
			{
				client.askNextProposition();
			}
			break;
		case ACTION_THEME_DEFAULT:
			game.setTheme("default/");
			frame.displayBoard(game);
			break;
		case ACTION_THEME_POKEMON:
			game.setTheme("pokemon/");
			frame.displayBoard(game);
			break;
		default:
			System.out.println("Unknow Action");
			break;
		}
		frame.setFocusOnBoard();
	}

	private boolean canMove() {
		if(server!=null && !server.isHand()){
			return false;
		}
		else if(client!=null && !client.isHand()){
			return false;
		}
		else
			return true;
	}

	public void mouseClicked(final MouseEvent e) {
		int round=game.getCurrentRound();
		int line = e.getY()/Constant.CASE_SIZE;
		int column = e.getX()/Constant.CASE_SIZE;


		if(canMove())
		{
			for(Robot r : game.getRobots()){
				if(column == r.x && line == r.y){
					game.setSelectedRobot(r);
					refreshBoard();
					return;
				}
			}

			if(game.getSelectedRobot() != null){
				if(game.getSelectedRobot().x == column){
					if(game.getSelectedRobot().y > line) { moveRobotInDirection(Direction.Up);}
					else {moveRobotInDirection(Direction.Down);}
				}
				else if(game.getSelectedRobot().y == line){
					if(game.getSelectedRobot().x > column) { moveRobotInDirection(Direction.Left); }
					else { moveRobotInDirection(Direction.Right);}
				}
			}	
		}
		
		if(server!=null)
		{
			checkEndRound(round);
		}
	}

	private void checkEndRound(int round) {
		
		if(round!=game.getCurrentRound())
		{
			server.getCount().stopCountdown();
			server.setStartCount(false);
			server.getCount().resetCountdown();
			server.addPoint();
			server.resetPropositions();
		}
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(final KeyEvent e) {
		
		int round=game.getCurrentRound();
		
		if(canMove())
		{		
			switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:
				moveRobotInDirection(Direction.Up);
				break;
			case KeyEvent.VK_DOWN:
				moveRobotInDirection(Direction.Down);
				break;
			case KeyEvent.VK_RIGHT:
				moveRobotInDirection(Direction.Right);
				break;
			case KeyEvent.VK_LEFT:
				moveRobotInDirection(Direction.Left);
				break;
			case KeyEvent.VK_1:
			case KeyEvent.VK_R :
				game.setSelectedRobot(Color.Red);
				break;
			case KeyEvent.VK_2:
			case KeyEvent.VK_G :
				game.setSelectedRobot(Color.Green);
				break;
			case KeyEvent.VK_3:
			case KeyEvent.VK_B :
				game.setSelectedRobot(Color.Blue);
				break;
			case KeyEvent.VK_4:
			case KeyEvent.VK_Y :
				game.setSelectedRobot(Color.Yellow);
				break;
			default:
				break;
			}
			
			if(server!=null)
			{
				checkEndRound(round);
			}
			
			refreshBoard();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		frame.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void setBoard(BoardPiece boardPiece) {
		boardPiece.initBoardPiece();
		game.getBoard().setBoardPiece(boardPiece);
		game.getBoard().setBoard();
	}

	/**
	 * Redirect System.out to the Frame
	 */
	public void write(int arg0) throws IOException {
		frame.println(String.valueOf((char) arg0));
	}

	public void setRobot(Robot r) {
		game.setRobotByColor(r.getColor(), r);
	}

	public void refreshBoard() {
		frame.displayBoard(game);
	}

	public void refreshColumn() {
		frame.displayDataInfo(game,this);
	}

	public Server getServer() {
		return server;
	}

	public Client getClient() {
		return client;
	}

	public void refreshPlayers(String user) {
		frame.displayPlayers(user);
	}

	public void setCurrentGoal(Stack<Box> goal) {
		game.setCurrentGoal(goal.pop());
		game.setmGoalCards(goal);
	}

	public void refreshClientCountDown() {
		if(server!=null)
		{
			server.sendTimeToAllClients();
		}
	}

	public void setSelectedRobot(Color c) {
		game.setSelectedRobot(c);
	}

	public void nextPlayerProposition() {
		server.nextPlayerProposition();
		
	}

	public void setOnlinePerspective(boolean b) {
		frame.setOnlinePerspective(b);
		
	}

	public void setEnabledforfeit(boolean b) {
		frame.setEnabledForfeit(b);
		
	}
	
	public void setEnabledValidate(boolean b) {
		frame.setEnabledValidate(b);
		
	}

	public Countdown getCount() {
		if (server!=null)
			return server.getCount();
		else if (client!=null)
			return client.getCount();
		else
		{
			Countdown c= new Countdown(this);
			c.setTime(0);
			return c;
		}
			
	}

}