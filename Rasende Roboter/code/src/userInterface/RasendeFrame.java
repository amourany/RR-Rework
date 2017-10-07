package userInterface;

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
import game.Box;
import game.Constant;
import game.Game;

/**
 * The window of the application with all its components and views
 */
public class RasendeFrame extends JFrame implements RasendeViewInterface {
    private static final long serialVersionUID = 4716072661083101699L;

    private JPanel contentPane, mBoardPanel, mColumnPanel, moveNB, userPanel;
    private JLabel mLabelRound, mLabelMove, mLabelTime;
    private JButton bSolution, bValidate, bPrevious, bNext, bForfeit;
    private JTextField tSuggestion;
    private JTextArea mConsoleText, user;
    private JMenuItem mJoinItem, mStartItem, mDisconnectItem;

    /**
     * Constructor of the class
     *
     * @param controller
     *            : Controller
     */
    public RasendeFrame(final Controller controller) {
        super();

        this.setSize(Constant.FRAME_WIDTH, Constant.FRAME_HEIGHT);
        this.setTitle(Constant.FRAME_TITLE);

        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(Constant.THEME_PATH + "default/robots/robotRed.png"));

        this.addWindowListener(controller);

        this.buildFrame(controller);
    }

    /**
     * Build all Panels
     *
     * @param controller
     */
    public void buildFrame(final Controller controller) {
        this.contentPane = new JPanel();
        this.contentPane.setLayout(new BoxLayout(this.contentPane, BoxLayout.LINE_AXIS));

        this.mColumnPanel = new JPanel();
        this.mColumnPanel.setBackground(java.awt.Color.WHITE);
        this.mColumnPanel.setLayout(new BoxLayout(this.mColumnPanel, BoxLayout.Y_AXIS));
        this.mColumnPanel.setPreferredSize(new Dimension(Constant.COLUMN_WIDTH, Constant.BOARD_SIZE));
        this.contentPane.add(this.mColumnPanel);

        this.mBoardPanel = new JPanel();
        this.mBoardPanel.setBackground(java.awt.Color.WHITE);
        this.mBoardPanel.setLayout(new GridLayout(Constant.NB_BOXES, Constant.NB_BOXES));
        this.mBoardPanel.setPreferredSize(new Dimension(Constant.BOARD_SIZE, Constant.BOARD_SIZE));
        this.mBoardPanel.addMouseListener(controller);
        this.contentPane.add(this.mBoardPanel);
        this.contentPane.addKeyListener(controller);
        this.contentPane.setFocusable(true);
        this.setFocusOnBoard();

        this.buildJMenu(controller);
        this.buildColumn(controller);

        this.setContentPane(this.contentPane);

        this.pack();

        this.display(controller.getgame(), controller);
        this.setVisible(true);
    }

    @Override
    public void setFocusOnBoard() {
        this.contentPane.requestFocusInWindow();
    }

    /**
     * Build the Menu Bar with all items
     *
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

        this.mJoinItem = new JMenuItem("Join Server");
        this.mJoinItem.setActionCommand(Controller.ACTION_JOIN_SERVER);
        this.mJoinItem.addActionListener(controller);
        menu.add(this.mJoinItem);

        this.mStartItem = new JMenuItem("Start Server");
        this.mStartItem.setActionCommand(Controller.ACTION_START_SERVER);
        this.mStartItem.addActionListener(controller);
        menu.add(this.mStartItem);

        this.mDisconnectItem = new JMenuItem("Disconnect");
        this.mDisconnectItem.setActionCommand(Controller.DISCONNECT);
        this.mDisconnectItem.addActionListener(controller);
        this.mDisconnectItem.setVisible(false);
        menu.add(this.mDisconnectItem);

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

        this.setJMenuBar(menuBar);
    }

    /**
     * Build the left Column
     *
     * @param controller
     * @see Controller
     */
    private void buildColumn(final Controller controller) {
        this.mLabelRound = new JLabel("Current Round: " + controller.getgame().getCurrentRound() + "/17");
        this.mLabelRound.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.mColumnPanel.add(this.mLabelRound);

        this.mLabelMove = new JLabel("Movements: " + 0);
        this.mLabelMove.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.mColumnPanel.add(this.mLabelMove);

        this.mLabelTime = new JLabel("Countdown: " + Constant.TIMER + " sec");
        this.mLabelTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.mLabelTime.setVisible(false);
        this.mColumnPanel.add(this.mLabelTime);

        // -------------------------------------------------------------
        // Number of movement proposed
        // -------------------------------------------------------------
        this.moveNB = new JPanel();
        this.moveNB.setVisible(false);
        this.moveNB.setBackground(java.awt.Color.WHITE);
        this.moveNB.setLayout(new FlowLayout());

        JLabel labelTF = new JLabel("Number of moves:");
        this.moveNB.add(labelTF);

        this.tSuggestion = new JTextField();
        this.tSuggestion.setPreferredSize(new Dimension(60, 30));
        this.moveNB.add(this.tSuggestion);
        this.mColumnPanel.add(this.moveNB);

        this.bValidate = new JButton("Validate");
        this.bValidate.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.bValidate.setActionCommand(Controller.ACTION_VALIDATE);
        this.bValidate.addActionListener(controller);
        this.bValidate.setVisible(false);
        this.mColumnPanel.add(this.bValidate);

        // -------------------------------------------------------------
        // Previous - Next Movement
        // -------------------------------------------------------------
        JPanel jp = new JPanel();
        jp.setBackground(java.awt.Color.WHITE);
        jp.setLayout(new FlowLayout());
        this.bPrevious = new JButton("<");
        this.bPrevious.setEnabled(false);
        this.bPrevious.setActionCommand(Controller.ACTION_PREVIOUS);
        this.bPrevious.addActionListener(controller);
        jp.add(this.bPrevious);
        this.bNext = new JButton(">");
        this.bNext.setEnabled(false);
        this.bNext.setActionCommand(Controller.ACTION_NEXT);
        this.bNext.addActionListener(controller);
        jp.add(this.bNext);
        this.mColumnPanel.add(jp);

        // -------------------------------------------------------------
        // Forfeit
        // -------------------------------------------------------------
        this.bForfeit = new JButton("Forfeit");
        this.bForfeit.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.bForfeit.setActionCommand(Controller.ACTION_FORFEIT);
        this.bForfeit.addActionListener(controller);
        this.mColumnPanel.add(this.bForfeit);

        // -------------------------------------------------------------
        // Solution
        // -------------------------------------------------------------
        this.bSolution = new JButton("Solution");
        this.bSolution.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.bSolution.setActionCommand(Controller.ACTION_SOLVE);
        this.bSolution.addActionListener(controller);
        this.mColumnPanel.add(this.bSolution);

        // -------------------------------------------------------------
        // Users
        // -------------------------------------------------------------
        this.userPanel = new JPanel();
        this.userPanel.setBackground(java.awt.Color.WHITE);
        this.userPanel.setLayout(new BoxLayout(this.userPanel, BoxLayout.PAGE_AXIS));
        this.userPanel.add(new JLabel("Users"));
        this.userPanel.setVisible(false);

        this.user = new JTextArea();
        this.user.setEditable(false);
        this.user.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(this.user);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.userPanel.add(scrollPane);
        this.user.setRows(10);
        this.mColumnPanel.add(this.userPanel);

        // -------------------------------------------------------------
        // Console
        // -------------------------------------------------------------
        JPanel console = new JPanel();
        console.setBackground(java.awt.Color.WHITE);
        console.setLayout(new BoxLayout(console, BoxLayout.PAGE_AXIS));
        console.add(new JLabel("Console"));

        this.mConsoleText = new JTextArea();
        this.mConsoleText.setEditable(false);
        this.mConsoleText.setLineWrap(true);

        JScrollPane scrollPane2 = new JScrollPane(this.mConsoleText);
        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        console.add(scrollPane2);
        this.mConsoleText.setRows(20);
        this.mColumnPanel.add(console);
    }

    /**
     * Remove all component and repaint it
     *
     * @param game
     *            : current game
     * @param c
     *            : Controller
     * @see Game
     * @see Controller
     */
    @Override
    public void display(final Game g, final Controller c) {
        this.displayBoard(g);
        this.displayDataInfo(g, c);
    }

    /**
     * Repaint the left column
     *
     * @param g
     *            : Game
     * @param c
     *            : Controller
     * @see Game
     * @see Controller
     */
    @Override
    public void displayDataInfo(final Game g, final Controller c) {
        this.mLabelRound.setText("Current Round: " + g.getCurrentRound() + "/17");
        this.mLabelMove.setText("Movements: " + g.getmPreviousPosition().size());
        this.mLabelTime.setText("Countdown: " + c.getCount().getTime() + " sec");

        this.bPrevious.setEnabled(g.hasPreviousPosition());
        this.bNext.setEnabled(g.hasNextPosition());
    }

    /**
     * Add the player list and points
     *
     * @param data
     *            : String containing the list of players
     */
    @Override
    public void displayPlayers(final String data) {
        this.user.setText(data);
    }

    /**
     * Repaint the Board We use a girdlayout to view the boxes
     *
     * @param game
     *            : current game
     * @see Game
     */
    @Override
    public void displayBoard(final Game game) {
        this.mBoardPanel.removeAll();

        Box[][] board = game.getBoard().getGameBoard();

        for (int i = 0; i < Constant.NB_BOXES; i++) {
            for (int j = 0; j < Constant.NB_BOXES; j++) {
                this.mBoardPanel.add(board[i][j].getJPanel(game, i, j));
            }
        }
        this.mBoardPanel.validate();
    }

    /**
     * Refresh the Help View
     */
    @Override
    public void displayHelp() {
        JOptionPane.showMessageDialog(this, "I) General Principle\n"
                + "The game consists of a board of 16 * 16 boxes and four robots of different colors (red, green, blue, yellow).\n"
                + "Some boxes have a type corresponding to the different objectives with robots.\n"
                + "There are four types of patterns, each time declined in the four colors for each of the robots.\n"
                + "In addition to these 16 boxes objectives, there is a special multicolored box can be reached by any of the robots.\n\n"
                + "II) Movements\n"
                + "To select a robot, you must use he mouse or the keyboard (R, G, B or Y, depending on color).\n"
                + "The robots move in a straight line and only stop when they encounter an obstacle (another robot, card edge or wall).\n"
                + "To move the robot must use the arrow keys or the mouse to click on the column or row in the direction where you want to go.\n\n"
                + "III) Scoring\n"
                + "With a solo game, the goal is to reach the 17 boxes with less movement possible.\n"
                + "With a multiplayer game, when a player thinks he has found a solution, he recorded his proposed number of movements:\n"
                + "from this moment other players have two minutes to find a better solution.\n"
                + "At the end of the allowed time, the one who had the best solution must show his solution to others.\n"
                + "If he succeeds, he wins the round. Otherwise it is the second to show its solution and so on.\n\n"
                + "The winner is the one who won the largest number of rounds.", "Help",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Redirection of the console in graphic mode
     *
     * @param s
     *            : a String
     */
    @Override
    public void println(final String s) {
        this.mConsoleText.append(s);
        this.mConsoleText.setCaretPosition(this.mConsoleText.getDocument().getLength());
    }

    /**
     * Change the perspective according if the game is online or not
     *
     * @param online
     *            <code>true</code> if the game is online
     */
    @Override
    public void setOnlinePerspective(Boolean online) {
        this.moveNB.setVisible(online);
        this.userPanel.setVisible(online);
        this.bValidate.setVisible(online);
        this.bForfeit.setVisible(online);
        this.mDisconnectItem.setVisible(online);
        this.mLabelTime.setVisible(online);
        this.mStartItem.setVisible(!online);
        this.mJoinItem.setVisible(!online);
        this.bSolution.setVisible(!online);
    }

    /**
     * Change the button forfeit according if the player has the hand
     *
     * @param enable
     *            <code>true</code> if the player has the hand else
     *            <code>false</code>
     */
    @Override
    public void setEnabledForfeit(Boolean enable) {
        this.bForfeit.setEnabled(enable);
    }

    /**
     * Change the button validate if someone play
     *
     * @param enable
     *            <code>true</code> if the player has the hand else
     *            <code>false</code>
     */
    @Override
    public void setEnabledValidate(Boolean enable) {
        this.bValidate.setEnabled(enable);
    }

    /**
     * The suggestion that the player submit
     *
     * @return the suggestion if it is correct, <code>-1</code> otherwise
     */
    @Override
    public int getSuggestion() {
        try {
            return Integer.parseInt(this.tSuggestion.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "This is not a correct number format!", "Number",
                    JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    /**
     * ShowMessageDialog warning users that reached their maximum number of
     * movements
     */
    @Override
    public void displayMoveLimit() {
        JOptionPane.showMessageDialog(this,
                "The movement that you want to does not respect the constraint that you set for the goal.\n"
                        + "Please cancel some of them to choose another path.",
                "MoveLimit!", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * ShowMessageDialog warning users that win the game
     */
    @Override
    public void displayWin() {
        JOptionPane.showMessageDialog(this, "Congratulations!! You beat the game!", "Congratulations",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * ShowMessageDialog for the License
     */
    @Override
    public void displayLicense() {
        JOptionPane
                .showMessageDialog(
                        this, "License : GNU GPL v3\n\n" + "Olivier Braik\n" + "Alexandre Delesse\n"
                                + "Gaetan Lussagnet\n" + "Alexandre Mourany\n" + "Dimitri Ranc",
                        "v. 1.0", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * ShowMessageDialog to start a server and ask a username
     *
     * @return the username of the player who starts the server
     */
    @Override
    public String displayStartServer() {
        String userName = (String) JOptionPane.showInputDialog(this, "Your username :", "Start Server",
                JOptionPane.WARNING_MESSAGE, null, null, "Anonymous Server");
        return userName;
    }

    /**
     * ShowMessageDialog to join a server
     *
     * @return table of string <code>[0]</code> is the pseudo of the player
     *         <code>[1]</code> is the IP that the player wants to join
     */
    @Override
    public String[] displayJoinServer() {
        String[] infos = new String[2];

        JTextField pseudo = new JTextField("Anonymous Client");
        JTextField ip = new JTextField("127.0.0.1");

        Object[] message = { "Username:", pseudo, "Ip Server:", ip };

        int option = JOptionPane.showConfirmDialog(this, message, "Join Server", JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            infos[0] = pseudo.getText();
            infos[1] = ip.getText();
        }

        return infos;
    }

    /**
     *
     */
    @Override
    public void displayConnectionLost() {
        // TODO Auto-generated method stub

    }
}
