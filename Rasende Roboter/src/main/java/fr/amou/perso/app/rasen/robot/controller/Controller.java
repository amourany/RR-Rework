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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Stack;

import fr.amou.perso.app.rasen.robot.game.BoardPiece;
import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Constant;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;
import fr.amou.perso.app.rasen.robot.game.Game;
import fr.amou.perso.app.rasen.robot.game.Robot;
import fr.amou.perso.app.rasen.robot.network.Client;
import fr.amou.perso.app.rasen.robot.network.Countdown;
import fr.amou.perso.app.rasen.robot.network.Server;
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

        this.game = new Game(this);
        this.frame = new RasendeFrame(this);
        this.frame.setOnlinePerspective(false);
    }

    public void moveRobotInDirection(final Direction dir) {
        int x = -1, y = -1;

        if (this.game.getSelectedRobot() != null) {

            // if it is a network game and the player will reach its maximum number of
            // allowed movements
            if ((this.server != null && this.game.getmPreviousPosition().size() + 1 == this.server.getBestProposition()
                    + 1) || (this.client != null && this.game.getmPreviousPosition().size() + 1 == this.client
                            .getBestProposition() + 1)) {
                this.moveLimit();
            } else {

                if (this.client != null || this.server != null) {
                    x = this.game.getSelectedRobot().getX();
                    y = this.game.getSelectedRobot().getY();
                }

                this.game.moveSelectedRobot(dir);

                this.refreshBoard();
                this.frame.displayDataInfo(this.game, this);

                // if client, send movement to server (only if there is a new location)
                if (this.client != null && (this.game.getSelectedRobot().x != x || this.game
                        .getSelectedRobot().y != y)) {
                    this.client.sendMove(this.game.getCurrentRound());
                } else if (this.server != null && (this.game.getSelectedRobot().x != x || this.game
                        .getSelectedRobot().y != y)) {
                    this.server.sendUpdates();
                }

                if (this.game.isOver()) {
                    this.gameOver();
                }
            }
        }
    }

    private void askToQuit() {
        if (this.server != null) {
            this.server.getCount().stopCountdown();
        } else if (this.client != null) {
            this.client.getCount().stopCountdown();
        }
        this.frame.dispose();
        System.exit(0);
    }

    public void moveLimit() {
        this.frame.displayMoveLimit();
    }

    public void timeLimit() {

        if (this.server != null) {
            this.server.startPlay();
            // nextPlayerProposition();
        }
    }

    public void gameOver() {
        this.frame.displayWin();
        this.game.startNewGame();
        this.frame.display(this.game, this);
    }

    public void stopClient(String msg) {
        this.client.stopClient();
        this.client = null;
        this.frame.setOnlinePerspective(false);
        System.out.println(msg);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        switch (e.getActionCommand()) {
        case ACTION_PREVIOUS:
            this.game.loadPreviousPosition();
            this.frame.display(this.game, this);
            if (this.server != null) {
                this.server.sendUpdates();
            } else if (this.client != null) {
                this.client.sendMove(this.game.getCurrentRound());
            }
            break;
        case ACTION_NEXT:
            this.game.loadNextPosition();
            this.frame.display(this.game, this);
            if (this.server != null) {
                this.server.sendUpdates();
            } else if (this.client != null) {
                this.client.sendMove(this.game.getCurrentRound());
            }
            break;
        case ACTION_QUIT:
            if (this.server != null) {
                this.server.sendServerDisconnect();
            }
            this.askToQuit();
            break;
        case ACTION_NEW_GAME:
            if (this.client != null) {
                this.stopClient("New Solo Game");
            }
            this.game.startNewGame();
            this.frame.display(this.game, this);
            if (this.server != null) {
                this.server.sendBoardToAllClients();
            }
            break;
        case ACTION_HELP:
            this.frame.displayHelp();
            break;
        case ACTION_LICENSE:
            this.frame.displayLicense();
            break;
        case ACTION_SOLVE:
            this.game.startSolver();
            break;
        case ACTION_START_SERVER:
            if (this.client == null) {
                String user = this.frame.displayStartServer();
                if ((user != null) && (user.length() > 0)) {
                    this.server = new Server(this, user);
                    this.game = this.server.getGame();
                    this.game.startNewGame();
                    this.frame.setOnlinePerspective(true);
                    this.frame.display(this.game, this);
                }
            }
            break;
        case ACTION_JOIN_SERVER:
            if (this.server == null) {
                String[] infos = this.frame.displayJoinServer();
                if (infos[0] != null) {
                    try {
                        this.client = new Client(InetAddress.getByName(infos[1]), this, infos[0]);
                        this.client.connect();
                    } catch (UnknownHostException e1) {
                        // e1.printStackTrace();
                        System.out.println("Bad IPAdress, please retry");
                    }
                }
            }
            break;

        case DISCONNECT:
            if (this.client != null) {
                this.stopClient("You are disconnected");
            } else if (this.server != null) {
                this.server.sendServerDisconnect();
                this.server.stopServer();
                this.server = null;
                this.frame.setOnlinePerspective(false);
            }
            this.game.startNewGame();
            this.frame.display(this.game, this);
            break;
        case ACTION_VALIDATE:
            if ((this.server != null || this.client != null)) {
                int suggestion = this.frame.getSuggestion();
                if (suggestion >= 0) {
                    if (this.server != null) {
                        this.server.updatePropositionfromPlayer(this.server.getUsername(), suggestion);
                    } else if (this.client != null) {
                        this.client.sendPropositionfromPlayer(this.client.getUsername(), suggestion);
                    }
                }
            }
            break;
        case ACTION_FORFEIT:
            if (this.server != null) {
                this.server.nextPlayerProposition();
            } else if (this.client != null) {
                this.client.askNextProposition();
            }
            break;
        case ACTION_THEME_DEFAULT:
            this.game.setTheme("default/");
            this.frame.displayBoard(this.game);
            break;
        case ACTION_THEME_POKEMON:
            this.game.setTheme("pokemon/");
            this.frame.displayBoard(this.game);
            break;
        default:
            System.out.println("Unknow Action");
            break;
        }
        this.frame.setFocusOnBoard();
    }

    private boolean canMove() {
        if (this.server != null && !this.server.isHand()) {
            return false;
        } else if (this.client != null && !this.client.isHand()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        int round = this.game.getCurrentRound();
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

        if (this.server != null) {
            this.checkEndRound(round);
        }
    }

    private void checkEndRound(int round) {

        if (round != this.game.getCurrentRound()) {
            this.server.getCount().stopCountdown();
            this.server.setStartCount(false);
            this.server.getCount().resetCountdown();
            this.server.addPoint();
            this.server.resetPropositions();
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

        int round = this.game.getCurrentRound();

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

            if (this.server != null) {
                this.checkEndRound(round);
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

    public void refreshClientCountDown() {
        if (this.server != null) {
            this.server.sendTimeToAllClients();
        }
    }

    public void setSelectedRobot(Color c) {
        this.game.setSelectedRobot(c);
    }

    public void nextPlayerProposition() {
        this.server.nextPlayerProposition();

    }

    public void setOnlinePerspective(boolean b) {
        this.frame.setOnlinePerspective(b);

    }

    public void setEnabledforfeit(boolean b) {
        this.frame.setEnabledForfeit(b);

    }

    public void setEnabledValidate(boolean b) {
        this.frame.setEnabledValidate(b);

    }

    public Countdown getCount() {
        if (this.server != null) {
            return this.server.getCount();
        } else if (this.client != null) {
            return this.client.getCount();
        } else {
            Countdown c = new Countdown(this);
            c.setTime(0);
            return c;
        }

    }

}
