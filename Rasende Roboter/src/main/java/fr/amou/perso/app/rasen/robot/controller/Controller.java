package fr.amou.perso.app.rasen.robot.controller;

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
import java.util.Stack;

import fr.amou.perso.app.rasen.robot.game.BoardPiece;
import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Constant;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;
import fr.amou.perso.app.rasen.robot.game.Game;
import fr.amou.perso.app.rasen.robot.game.Robot;
import fr.amou.perso.app.rasen.robot.userInterface.RasendeFrame;
import fr.amou.perso.app.rasen.robot.userInterface.RasendeViewInterface;
import lombok.Data;

@Data
public class Controller extends OutputStream implements ActionListener, MouseListener, KeyListener, WindowListener {
    public final static String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    public final static String ACTION_NEXT = "ACTION_NEXT";
    public final static String ACTION_QUIT = "ACTION_QUIT";
    public final static String ACTION_NEW_GAME = "ACTION_NEW_GAME";
    public final static String ACTION_HELP = "ACTION_HELP";
    public final static String ACTION_SOLVE = "ACTION_SOLVE";
    public final static String ACTION_THEME_DEFAULT = "ACTION_THEME_DEFAULT";

    private Game game;
    private final RasendeViewInterface frame;

    public Controller() {
        super();

        final PrintStream out = new PrintStream(this);
        System.setOut(out);

        this.game = new Game(this);
        this.frame = new RasendeFrame(this);
    }

    public void moveRobotInDirection(final Direction dir) {

        if (this.game.getSelectedRobot() != null) {

            this.game.moveSelectedRobot(dir);

            this.refreshBoard();
            this.frame.displayDataInfo(this.game, this);

            if (this.game.isOver()) {
                this.gameOver();
            }
        }
    }

    private void askToQuit() {
        this.frame.dispose();
        System.exit(0);
    }

    public void moveLimit() {
        this.frame.displayMoveLimit();
    }

    public void gameOver() {
        this.frame.displayWin();
        this.game.startNewGame();
        this.frame.display(this.game, this);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        switch (e.getActionCommand()) {
        case ACTION_PREVIOUS:
            this.game.loadPreviousPosition();
            this.frame.display(this.game, this);
            break;
        case ACTION_NEXT:
            this.game.loadNextPosition();
            this.frame.display(this.game, this);
            break;
        case ACTION_QUIT:
            this.askToQuit();
            break;
        case ACTION_NEW_GAME:
            this.game.startNewGame();
            this.frame.display(this.game, this);
            break;
        case ACTION_HELP:
            this.frame.displayHelp();
            break;
        case ACTION_SOLVE:
            this.game.startSolver();
            break;
        case ACTION_THEME_DEFAULT:
            this.game.setTheme("default/");
            this.frame.displayBoard(this.game);
            break;
        default:
            System.out.println("Unknow Action");
            break;
        }
        this.frame.setFocusOnBoard();
    }

    private boolean canMove() {
        return true;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        int line = e.getY() / Constant.CASE_SIZE;
        int column = e.getX() / Constant.CASE_SIZE;

        if (this.canMove()) {
            for (Robot r : this.game.getRobots()) {
                if (column == r.x && line == r.y) {
                    this.game.setSelectedRobot(r);
                    this.refreshBoard();
                    return;
                }
            }

            if (this.game.getSelectedRobot() != null) {
                if (this.game.getSelectedRobot().x == column) {
                    if (this.game.getSelectedRobot().y > line) {
                        this.moveRobotInDirection(Direction.Up);
                    } else {
                        this.moveRobotInDirection(Direction.Down);
                    }
                } else if (this.game.getSelectedRobot().y == line) {
                    if (this.game.getSelectedRobot().x > column) {
                        this.moveRobotInDirection(Direction.Left);
                    } else {
                        this.moveRobotInDirection(Direction.Right);
                    }
                }
            }
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(final KeyEvent e) {

        if (this.canMove()) {
            switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.moveRobotInDirection(Direction.Up);
                break;
            case KeyEvent.VK_DOWN:
                this.moveRobotInDirection(Direction.Down);
                break;
            case KeyEvent.VK_RIGHT:
                this.moveRobotInDirection(Direction.Right);
                break;
            case KeyEvent.VK_LEFT:
                this.moveRobotInDirection(Direction.Left);
                break;
            case KeyEvent.VK_1:
            case KeyEvent.VK_R:
                this.game.setSelectedRobot(Color.Red);
                break;
            case KeyEvent.VK_2:
            case KeyEvent.VK_G:
                this.game.setSelectedRobot(Color.Green);
                break;
            case KeyEvent.VK_3:
            case KeyEvent.VK_B:
                this.game.setSelectedRobot(Color.Blue);
                break;
            case KeyEvent.VK_4:
            case KeyEvent.VK_Y:
                this.game.setSelectedRobot(Color.Yellow);
                break;
            default:
                break;
            }

            this.refreshBoard();
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
        this.frame.dispose();
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
        this.game.getBoard().setBoardPiece(boardPiece);
        this.game.getBoard().setBoard();
    }

    /**
     * Redirect System.out to the Frame
     */
    @Override
    public void write(int arg0) throws IOException {
        this.frame.println(String.valueOf((char) arg0));
    }

    public void setRobot(Robot r) {
        this.game.setRobotByColor(r.getColor(), r);
    }

    public void refreshBoard() {
        this.frame.displayBoard(this.game);
    }

    public void refreshColumn() {
        this.frame.displayDataInfo(this.game, this);
    }

    public void refreshPlayers(String user) {
        this.frame.displayPlayers(user);
    }

    public void setCurrentGoal(Stack<Box> goal) {
        this.game.setCurrentGoal(goal.pop());
        this.game.setmGoalCards(goal);
    }

    public void setSelectedRobot(Color c) {
        this.game.setSelectedRobot(c);
    }

    public void setEnabledValidate(boolean b) {
        this.frame.setEnabledValidate(b);

    }

}
