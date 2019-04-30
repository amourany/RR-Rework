package fr.amou.perso.app.rasen.robot.game.manager;

import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;

public interface GameManager {

	void run();

	void moveRobotInDirection(DirectionDeplacementEnum dir);

	void gameOver();

	void setSelectedRobot(ColorRobotEnum c);

	void handleMouseAction(int line, int column);

	void loadPreviousPosition();

	void loadNextPosition();

	void startNewGame();

	void startSolver();

	void refreshScreen();

}
