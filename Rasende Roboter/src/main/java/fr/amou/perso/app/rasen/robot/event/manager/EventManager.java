package fr.amou.perso.app.rasen.robot.event.manager;

import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;

public interface EventManager {

    void moveRobotInDirection(DirectionDeplacementEnum dir);

    void gameOver();

    // void refreshBoard();

    void refreshColumn();

    // void setCurrentGoal(Stack<Box> goal);

    void setSelectedRobot(ColorRobotEnum c);

    void loadPreviousPosition();

    void loadNextPosition();

    void askToQuit();

    void startNewGame();

    void displayHelp();

    void startSolver();

    void handleMouseAction(int line, int column);

    void println(String valueOf);

}
