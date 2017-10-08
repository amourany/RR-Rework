package fr.amou.perso.app.rasen.robot.event.manager;

import java.util.Stack;

import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;
import fr.amou.perso.app.rasen.robot.game.Robot;

public interface ControllerService {

    void run() throws Exception;

    void moveRobotInDirection(Direction dir);

    void gameOver();

    void setRobot(Robot r);

    void refreshBoard();

    void refreshColumn();

    void setCurrentGoal(Stack<Box> goal);

    void setSelectedRobot(Color c);

    void loadPreviousPosition();

    void loadNextPosition();

    void askToQuit();

    void startNewGame();

    void displayHelp();

    void startSolver();

    void handleMouseAction(int line, int column);

}
