package fr.amou.perso.app.rasen.robot.event.manager;

import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.amou.perso.app.rasen.robot.game.BoardPiece;
import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;
import fr.amou.perso.app.rasen.robot.game.Game;
import fr.amou.perso.app.rasen.robot.game.Robot;
import fr.amou.perso.app.rasen.robot.userInterface.RasendeViewInterface;

@Service
public class ControllerDefaultService implements ControllerService {

    @Autowired
    private Game game;

    @Autowired
    private RasendeViewInterface frame;

    @Override
    public void run() throws Exception {
        this.game.startNewGame();
        this.frame.buildFrame();
    }

    @Override
    public void moveRobotInDirection(final Direction dir) {

        if (this.game.getSelectedRobot() != null) {

            this.game.moveSelectedRobot(dir);

            this.refreshBoard();
            this.frame.displayDataInfo();

            if (this.game.isOver()) {
                this.gameOver();
            }
        }
    }

    @Override
    public void askToQuit() {
        this.frame.dispose();
        System.exit(0);
    }

    @Override
    public void gameOver() {
        this.frame.displayWin();
        this.game.startNewGame();
        this.frame.display();
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
    public void println(String message) {
        this.frame.println(String.valueOf(message));
    }

    @Override
    public void setRobot(Robot r) {
        this.game.setRobotByColor(r.getColor(), r);
    }

    @Override
    public void refreshBoard() {
        this.frame.displayBoard();
    }

    @Override
    public void refreshColumn() {
        this.frame.displayDataInfo();
    }

    @Override
    public void setCurrentGoal(Stack<Box> goal) {
        this.game.setCurrentGoal(goal.pop());
        this.game.setmGoalCards(goal);
    }

    @Override
    public void setSelectedRobot(Color c) {
        this.game.setSelectedRobot(c);
    }

    @Override
    public void loadPreviousPosition() {
        this.game.loadPreviousPosition();
        this.frame.display();
    }

    @Override
    public void loadNextPosition() {
        this.game.loadNextPosition();
        this.frame.display();
    }

    @Override
    public void startNewGame() {
        this.game.startNewGame();
        this.frame.display();
    }

    @Override
    public void displayHelp() {
        this.frame.displayHelp();

    }

    @Override
    public void startSolver() {
        this.game.startSolver();
        this.frame.setFocusOnBoard();
    }

    @Override
    public void handleMouseAction(int line, int column) {
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
