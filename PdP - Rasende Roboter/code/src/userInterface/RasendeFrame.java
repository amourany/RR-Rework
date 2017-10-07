package userInterface;


import game.Box;
import game.Constant;
import game.Game;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.Controller;



/**
 * The window of the application with all its components and views
 */
public class RasendeFrame extends JFrame implements RasendeViewInterface{
	private static final long serialVersionUID = 4716072661083101699L;

	private JPanel contentPane, mBoardPanel, mColumnPanel, moveNB, userPanel;
	private JLabel mLabelRound, mLabelMove, mLabelTime;
	private JButton bSolution, bValidate, bPrevious, bNext, bForfeit;
	private JTextField tSuggestion;
	private JTextArea mConsoleText, user;
	private JMenuItem mJoinItem, mStartItem, mDisconnectItem;

	/**
	 * Constructor of the class
	 * @param controller : Controller
	 */
	public RasendeFrame(final Controller controller){
		super();

		setSize(Constant.FRAME_WIDTH, Constant.FRAME_HEIGHT);
		setTitle(Constant.FRAME_TITLE);

		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Constant.THEME_PATH + "default/robots/robotRed.png"));

		addWindowListener(controller);
		
		buildFrame(controller);
	}

	/**
	 * Build all Panels
	 * @param controller
	 */
	public void buildFrame(final Controller controller){
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.LINE_AXIS));

		mColumnPanel = new JPanel();
		mColumnPanel.setBackground(java.awt.Color.WHITE);
		mColumnPanel.setLayout(new BoxLayout(mColumnPanel, BoxLayout.Y_AXIS));
		mColumnPanel.setPreferredSize(new Dimension(Constant.COLUMN_WIDTH,Constant.BOARD_SIZE));
		contentPane.add(mColumnPanel);

		mBoardPanel = new JPanel();
		mBoardPanel.setBackground(java.awt.Color.WHITE);
		mBoardPanel.setLayout(new GridLayout(Constant.NB_BOXES, Constant.NB_BOXES));
		mBoardPanel.setPreferredSize(new Dimension(Constant.BOARD_SIZE,Constant.BOARD_SIZE));
		mBoardPanel.addMouseListener(controller);
		contentPane.add(mBoardPanel);	
		contentPane.addKeyListener(controller);
		contentPane.setFocusable(true);
		setFocusOnBoard();

		buildJMenu(controller);
		buildColumn(controller);

		setContentPane(contentPane);

		pack();
		
		display(controller.getgame(),controller);
		setVisible(true);
	}

	public void setFocusOnBoard() {
		contentPane.requestFocusInWindow();
	}

	/**
	 * Build the Menu Bar with all items
	 * @param controller
	 * @see Controller
	 */
	private void buildJMenu(final Controller controller) {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("Game");
		menuBar.add(menu);

		menuItem = new JMenuItem("New Game");
		menuItem.setActionCommand(Controller.ACTION_NEW_GAME);
		menuItem.addActionListener(controller);
		menu.add(menuItem);

		menuItem = new JMenuItem("Help");
		menuItem.setActionCommand(Controller.ACTION_HELP);
		menuItem.addActionListener(controller);
		menu.add(menuItem);		

		menuItem = new JMenuItem("License");
		menuItem.setActionCommand(Controller.ACTION_LICENSE);
		menuItem.addActionListener(controller);
		menu.add(menuItem);	

		menu.addSeparator();

		menuItem = new JMenuItem("Quit");
		menuItem.setActionCommand(Controller.ACTION_QUIT);
		menuItem.addActionListener(controller);
		menu.add(menuItem);		

		menu = new JMenu("Online");
		menuBar.add(menu);

		mJoinItem = new JMenuItem("Join Server");
		mJoinItem.setActionCommand(Controller.ACTION_JOIN_SERVER);
		mJoinItem.addActionListener(controller);
		menu.add(mJoinItem);

		mStartItem = new JMenuItem("Start Server");
		mStartItem.setActionCommand(Controller.ACTION_START_SERVER);
		mStartItem.addActionListener(controller);
		menu.add(mStartItem);
		
		mDisconnectItem = new JMenuItem("Disconnect");
		mDisconnectItem.setActionCommand(Controller.DISCONNECT);
		mDisconnectItem.addActionListener(controller);
		mDisconnectItem.setVisible(false);
		menu.add(mDisconnectItem);

		menu = new JMenu("Theme");
		menuBar.add(menu);		

		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Default");
		rbMenuItem.setSelected(true);
		rbMenuItem.setActionCommand(Controller.ACTION_THEME_DEFAULT);
		rbMenuItem.addActionListener(controller);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Pokemon");
		rbMenuItem.setActionCommand(Controller.ACTION_THEME_POKEMON);
		rbMenuItem.addActionListener(controller);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		setJMenuBar(menuBar);
	}

	/**
	 * Build the left Column
	 * @param controller
	 * @see Controller
	 */
	private void buildColumn(final Controller controller){
		mLabelRound = new JLabel("Current Round: " + controller.getgame().getCurrentRound() + "/17");
		mLabelRound.setAlignmentX(Component.CENTER_ALIGNMENT);
		mColumnPanel.add(mLabelRound);

		mLabelMove = new JLabel("Movements: " + 0);
		mLabelMove.setAlignmentX(Component.CENTER_ALIGNMENT);
		mColumnPanel.add(mLabelMove);

		mLabelTime = new JLabel("Countdown: " + Constant.TIMER +" sec");
		mLabelTime.setAlignmentX(Component.CENTER_ALIGNMENT);
		mLabelTime.setVisible(false);
		mColumnPanel.add(mLabelTime);

		//-------------------------------------------------------------
		// Number of movement proposed
		//-------------------------------------------------------------
		moveNB = new JPanel();
		moveNB.setVisible(false);
		moveNB.setBackground(java.awt.Color.WHITE);
		moveNB.setLayout(new FlowLayout());

		JLabel labelTF= new JLabel("Number of moves:");
		moveNB.add(labelTF);

		tSuggestion = new JTextField();
		tSuggestion.setPreferredSize(new Dimension(60,30));
		moveNB.add(tSuggestion);
		mColumnPanel.add(moveNB);

		bValidate = new JButton("Validate");
		bValidate.setAlignmentX(Component.CENTER_ALIGNMENT);
		bValidate.setActionCommand(Controller.ACTION_VALIDATE);
		bValidate.addActionListener(controller);
		bValidate.setVisible(false);
		mColumnPanel.add(bValidate);

		//-------------------------------------------------------------
		// Previous - Next Movement
		//-------------------------------------------------------------
		JPanel jp = new JPanel();
		jp.setBackground(java.awt.Color.WHITE);
		jp.setLayout(new FlowLayout());
		bPrevious = new JButton("<");
		bPrevious.setEnabled(false);
		bPrevious.setActionCommand(Controller.ACTION_PREVIOUS);
		bPrevious.addActionListener(controller);
		jp.add(bPrevious);
		bNext = new JButton(">");
		bNext.setEnabled(false);
		bNext.setActionCommand(Controller.ACTION_NEXT);
		bNext.addActionListener(controller);
		jp.add(bNext);
		mColumnPanel.add(jp);

		//-------------------------------------------------------------
		// Forfeit
		//-------------------------------------------------------------
		bForfeit = new JButton("Forfeit");
		bForfeit.setAlignmentX(Component.CENTER_ALIGNMENT);
		bForfeit.setActionCommand(Controller.ACTION_FORFEIT);
		bForfeit.addActionListener(controller);
		mColumnPanel.add(bForfeit);		

				
		//-------------------------------------------------------------
		// Solution
		//-------------------------------------------------------------
		bSolution = new JButton("Solution");
		bSolution.setAlignmentX(Component.CENTER_ALIGNMENT);
		bSolution.setActionCommand(Controller.ACTION_SOLVE);
		bSolution.addActionListener(controller);
		mColumnPanel.add(bSolution);		

		//-------------------------------------------------------------
		// Users
		//-------------------------------------------------------------
		userPanel = new JPanel();
		userPanel.setBackground(java.awt.Color.WHITE);
		userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.PAGE_AXIS));
		userPanel.add(new JLabel("Users"));
		userPanel.setVisible(false);

		user = new JTextArea(); 
		user.setEditable(false);
		user.setLineWrap(true);

		JScrollPane scrollPane = new JScrollPane(user); 
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		userPanel.add(scrollPane);
		user.setRows(10);
		mColumnPanel.add(userPanel);	

		//-------------------------------------------------------------
		// Console
		//-------------------------------------------------------------
		JPanel console = new JPanel();
		console.setBackground(java.awt.Color.WHITE);
		console.setLayout(new BoxLayout(console, BoxLayout.PAGE_AXIS));
		console.add(new JLabel("Console"));

		mConsoleText = new JTextArea();
		mConsoleText.setEditable(false);
		mConsoleText.setLineWrap(true);

		JScrollPane scrollPane2 = new JScrollPane(mConsoleText); 
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		console.add(scrollPane2);
		mConsoleText.setRows(20);
		mColumnPanel.add(console);		
	}

	/**
	 * Remove all component and repaint it
	 * @param game : current game
	 * @param c : Controller
	 * @see Game
	 * @see Controller
	 */
	public void display(final Game g,final Controller c){
		displayBoard(g);
		displayDataInfo(g,c);
	}

	/**
	 * Repaint the left column
	 * @param g : Game
	 * @param c : Controller
	 * @see Game
	 * @see Controller
	 */
	public void displayDataInfo(final Game g, final Controller c){
		mLabelRound.setText("Current Round: " + g.getCurrentRound() + "/17");		
		mLabelMove.setText("Movements: " + g.getmPreviousPosition().size());
		mLabelTime.setText("Countdown: " + c.getCount().getTime() + " sec");

		bPrevious.setEnabled(g.hasPreviousPosition());
		bNext.setEnabled(g.hasNextPosition());		
	}

	/**
	 * Add the player list and points
	 * @param data : String containing the list of players
	 */
	public void displayPlayers(final String data) {
		user.setText(data);
	} 

	/**
	 * Repaint the Board
	 * We use a girdlayout to view the boxes
	 * @param game : current game
	 * @see Game
	 */
	public void displayBoard(final Game game){
		mBoardPanel.removeAll();

		Box[][] board = game.getBoard().getGameBoard();

		for(int i=0; i<Constant.NB_BOXES; i++){
			for(int j=0; j<Constant.NB_BOXES; j++){
				mBoardPanel.add(board[i][j].getJPanel(game, i, j));
			}
		}		
		mBoardPanel.validate();
	}

	/**
	 * Refresh the Help View
	 */
	public void displayHelp(){
		JOptionPane.showMessageDialog(this, "I) General Principle\n"+
				"The game consists of a board of 16 * 16 boxes and four robots of different colors (red, green, blue, yellow).\n"+
				"Some boxes have a type corresponding to the different objectives with robots.\n" +
				"There are four types of patterns, each time declined in the four colors for each of the robots.\n"+
				"In addition to these 16 boxes objectives, there is a special multicolored box can be reached by any of the robots.\n\n"+
				"II) Movements\n"+ 
				"To select a robot, you must use he mouse or the keyboard (R, G, B or Y, depending on color).\n"+
				"The robots move in a straight line and only stop when they encounter an obstacle (another robot, card edge or wall).\n"+
				"To move the robot must use the arrow keys or the mouse to click on the column or row in the direction where you want to go.\n\n"+
				"III) Scoring\n"+
				"With a solo game, the goal is to reach the 17 boxes with less movement possible.\n"+
				"With a multiplayer game, when a player thinks he has found a solution, he recorded his proposed number of movements:\n" +
				"from this moment other players have two minutes to find a better solution.\n"+
				"At the end of the allowed time, the one who had the best solution must show his solution to others.\n" +
				"If he succeeds, he wins the round. Otherwise it is the second to show its solution and so on.\n\n"+
				"The winner is the one who won the largest number of rounds.",
				"Help",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Redirection of the console in graphic mode
	 * @param s : a String
	 */
	public void println(final String s){
		mConsoleText.append(s);
		mConsoleText.setCaretPosition(mConsoleText.getDocument().getLength());
	}

	/**
	 * Change the perspective according if the game is online or not
	 * @param online <code>true</code> if the game is online
	 */
	public void setOnlinePerspective(Boolean online){
		moveNB.setVisible(online);		
		userPanel.setVisible(online);
		bValidate.setVisible(online);
		bForfeit.setVisible(online);
		mDisconnectItem.setVisible(online);
		mLabelTime.setVisible(online);
		mStartItem.setVisible(!online);
		mJoinItem.setVisible(!online);
		bSolution.setVisible(!online);
	}

	/**
	 * Change the button forfeit according if the player has the hand
	 * @param enable <code>true</code> if the player has the hand else <code>false</code>
	 */
	public void setEnabledForfeit(Boolean enable)
	{
	 bForfeit.setEnabled(enable);
	}
	
	/**
	 * Change the button validate if someone play
	 * @param enable <code>true</code> if the player has the hand else <code>false</code>
	 */
	public void setEnabledValidate(Boolean enable)
	{
	 bValidate.setEnabled(enable);
	}
	
	/**
	 * The suggestion that the player submit
	 * @return the suggestion if it is correct, <code>-1</code> otherwise
	 */
	public int getSuggestion(){
		try{
			return Integer.parseInt(tSuggestion.getText());
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"This is not a correct number format!",
					"Number",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}
	}
	
	/**
	 * ShowMessageDialog warning users that reached their maximum number of movements
	 */
	public void displayMoveLimit() {
		JOptionPane.showMessageDialog(this,
				"The movement that you want to does not respect the constraint that you set for the goal.\n" +
				"Please cancel some of them to choose another path.",
				"MoveLimit!",
				JOptionPane.INFORMATION_MESSAGE);		
	}

	/**
	 * ShowMessageDialog warning users that win the game
	 */
	public void displayWin() {
		JOptionPane.showMessageDialog(this,
				"Congratulations!! You beat the game!",
				"Congratulations",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * ShowMessageDialog for the License
	 */
	public void displayLicense() {
		JOptionPane.showMessageDialog(this,
				"License : GNU GPL v3\n\n"+
						"Olivier Braik\n" +
						"Alexandre Delesse\n" +
						"Gaetan Lussagnet\n" +
						"Alexandre Mourany\n" +
						"Dimitri Ranc",
						"v. 1.0",
						JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * ShowMessageDialog to start a server and ask a username
	 * @return the username of the player who starts the server
	 */
	public String displayStartServer() {
		String userName = (String)JOptionPane.showInputDialog(this,
				"Your username :",
				"Start Server",
				JOptionPane.WARNING_MESSAGE,null,null,"Anonymous Server");
		return userName;
	}

	/**
	 * ShowMessageDialog to join a server
	 * @return table of string <code>[0]</code> is the pseudo of the player
	 * <code>[1]</code> is the IP that the player wants to join
	 */
	public String[] displayJoinServer(){
		String[] infos = new String[2];

		JTextField pseudo = new JTextField("Anonymous Client");
		JTextField ip = new JTextField("127.0.0.1");

		Object[] message = {
				"Username:", pseudo,
				"Ip Server:", ip
		};

		int option = JOptionPane.showConfirmDialog(this,
				message,
				"Join Server",
				JOptionPane.WARNING_MESSAGE);

		if (option == JOptionPane.OK_OPTION) {
			infos[0] = pseudo.getText();
			infos[1] = ip.getText();
		}

		return infos;
	}

	/**
	 * 
	 */
	public void displayConnectionLost(){
		// TODO Auto-generated method stub
		
	}
}