package fr.amou.perso.app.rasen.robot.userInterface;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import fr.amou.perso.app.rasen.robot.enums.ActionPossibleEnum;
import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Constant;
import fr.amou.perso.app.rasen.robot.game.Game;

/**
 * The window of the application with all its components and views
 */
@Component
public class RasendeFrame implements RasendeViewInterface {

    private JPanel mBoardPanel, mColumnPanel;
    private JLabel mLabelRound, mLabelMove;
    private JButton bSolution, bPrevious, bNext;
    private JTextArea mConsoleText;

    private JFrame jFrame;

    @Autowired
    private ActionListener actionListener;

    @Autowired
    private KeyAdapter keyAdapter;

    @Autowired
    private MouseAdapter mouseAdapter;

    @Autowired
    private WindowAdapter windowAdapter;

    @Autowired
    private Game game;

    /**
     * Constructor of the class
     *
     * @param controller
     *            : Controller
     */
    public RasendeFrame() {

        this.jFrame = new JFrame();
        this.jFrame.setSize(Constant.FRAME_WIDTH, Constant.FRAME_HEIGHT);
        this.jFrame.setTitle(Constant.FRAME_TITLE);

        this.jFrame.setResizable(false);
        this.jFrame.setLocationRelativeTo(null);
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(Constant.THEME_PATH
                + "default/robots/robotRed.png"));

        this.jFrame.addWindowListener(this.windowAdapter);

        // this.buildFrame();
    }

    /**
     * Build all Panels
     *
     * @param controller
     */
    @Override
    public void buildFrame() {
        Container contentPane = this.jFrame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.LINE_AXIS));

        this.mColumnPanel = new JPanel();
        this.mColumnPanel.setBackground(java.awt.Color.WHITE);
        this.mColumnPanel.setLayout(new BoxLayout(this.mColumnPanel, BoxLayout.Y_AXIS));
        this.mColumnPanel.setPreferredSize(new Dimension(Constant.COLUMN_WIDTH, Constant.BOARD_SIZE));
        contentPane.add(this.mColumnPanel);

        this.mBoardPanel = new JPanel();
        this.mBoardPanel.setBackground(java.awt.Color.WHITE);
        this.mBoardPanel.setLayout(new GridLayout(Constant.NB_BOXES, Constant.NB_BOXES));
        this.mBoardPanel.setPreferredSize(new Dimension(Constant.BOARD_SIZE, Constant.BOARD_SIZE));
        this.mBoardPanel.addMouseListener(this.mouseAdapter);
        contentPane.add(this.mBoardPanel);
        contentPane.addKeyListener(this.keyAdapter);
        contentPane.setFocusable(true);
        this.setFocusOnBoard();

        this.buildJMenu();
        this.buildColumn();

        this.jFrame.setContentPane(contentPane);

        this.jFrame.pack();

        this.display();
        this.jFrame.setVisible(true);
    }

    @Override
    public void setFocusOnBoard() {
        this.jFrame.getContentPane().requestFocusInWindow();
    }

    /**
     * Build the Menu Bar with all items
     *
     * @param controller
     * @see Controller
     */
    private void buildJMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("Game");
        menuBar.add(menu);

        menuItem = new JMenuItem("New Game");
        menuItem.setActionCommand(ActionPossibleEnum.ACTION_NEW_GAME.name());
        menuItem.addActionListener(this.actionListener);
        menu.add(menuItem);

        menuItem = new JMenuItem("Help");
        menuItem.setActionCommand(ActionPossibleEnum.ACTION_HELP.name());
        menuItem.addActionListener(this.actionListener);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Quit");
        menuItem.setActionCommand(ActionPossibleEnum.ACTION_QUIT.name());
        menuItem.addActionListener(this.actionListener);
        menu.add(menuItem);

        menu = new JMenu("Theme");
        menuBar.add(menu);

        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Default");
        rbMenuItem.setSelected(true);
        rbMenuItem.setActionCommand(ActionPossibleEnum.ACTION_THEME_DEFAULT.name());
        rbMenuItem.addActionListener(this.actionListener);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        this.jFrame.setJMenuBar(menuBar);
    }

    /**
     * Build the left Column
     *
     * @param controller
     * @see Controller
     */
    private void buildColumn() {
        this.mLabelRound = new JLabel("Current Round: " + this.game.getCurrentRound() + "/17");
        this.mLabelRound.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        this.mColumnPanel.add(this.mLabelRound);

        this.mLabelMove = new JLabel("Movements: " + 0);
        this.mLabelMove.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        this.mColumnPanel.add(this.mLabelMove);

        // -------------------------------------------------------------
        // Previous - Next Movement
        // -------------------------------------------------------------
        JPanel jp = new JPanel();
        jp.setBackground(java.awt.Color.WHITE);
        jp.setLayout(new FlowLayout());
        this.bPrevious = new JButton("<");
        this.bPrevious.setEnabled(false);
        this.bPrevious.setActionCommand(ActionPossibleEnum.ACTION_PREVIOUS.name());
        this.bPrevious.addActionListener(this.actionListener);
        jp.add(this.bPrevious);
        this.bNext = new JButton(">");
        this.bNext.setEnabled(false);
        this.bNext.setActionCommand(ActionPossibleEnum.ACTION_NEXT.name());
        this.bNext.addActionListener(this.actionListener);
        jp.add(this.bNext);
        this.mColumnPanel.add(jp);

        // -------------------------------------------------------------
        // Solution
        // -------------------------------------------------------------
        this.bSolution = new JButton("Solution");
        this.bSolution.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        this.bSolution.setActionCommand(ActionPossibleEnum.ACTION_SOLVE.name());
        this.bSolution.addActionListener(this.actionListener);
        this.mColumnPanel.add(this.bSolution);

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
     * @see Game
     */
    @Override
    public void display() {
        this.displayBoard();
        this.displayDataInfo();
        this.setFocusOnBoard();
    }

    /**
     * Repaint the left column
     *
     * @param g
     *            : Game
     * @see Game
     */
    @Override
    public void displayDataInfo() {
        this.mLabelRound.setText("Current Round: " + this.game.getCurrentRound() + "/17");
        this.mLabelMove.setText("Movements: " + this.game.getmPreviousPosition().size());

        this.bPrevious.setEnabled(this.game.hasPreviousPosition());
        this.bNext.setEnabled(this.game.hasNextPosition());
    }

    /**
     * Repaint the Board We use a girdlayout to view the boxes
     *
     * @param game
     *            : current game
     * @see Game
     */
    @Override
    public void displayBoard() {
        this.mBoardPanel.removeAll();

        Box[][] board = this.game.getBoard().getGameBoard();

        for (int i = 0; i < Constant.NB_BOXES; i++) {
            for (int j = 0; j < Constant.NB_BOXES; j++) {
                this.mBoardPanel.add(board[i][j].getJPanel(this.game, i, j));
            }
        }
        this.mBoardPanel.validate();
    }

    /**
     * Refresh the Help View
     */
    @Override
    public void displayHelp() {
        JOptionPane.showMessageDialog(this.jFrame, "I) General Principle\n"
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
     * ShowMessageDialog warning users that win the game
     */
    @Override
    public void displayWin() {
        JOptionPane.showMessageDialog(this.jFrame, "Congratulations!! You beat the game!", "Congratulations",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void dispose() {
        this.jFrame.dispose();
    }

}
