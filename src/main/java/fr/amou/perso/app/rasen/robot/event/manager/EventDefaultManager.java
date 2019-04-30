package fr.amou.perso.app.rasen.robot.event.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.game.manager.GameManager;
import fr.amou.perso.app.rasen.robot.userInterface.RasendeViewInterface;

@Service
public class EventDefaultManager implements EventManager {

	@Autowired
	private RasendeViewInterface frame;

	@Autowired
	private GameManager gameManager;

	@Override
	public void moveRobotInDirection(final DirectionDeplacementEnum dir) {
		this.gameManager.moveRobotInDirection(dir);
		this.frame.displayBoard();
	}

	@Override
	public void askToQuit() {
		this.frame.dispose();
		System.exit(0);
	}

	@Override
	public void gameOver() {
		this.gameManager.gameOver();
	}

	/**
	 * Redirect System.out to the Frame
	 */
	@Override
	public void afficherMessage(String message) {
		this.frame.afficherMessage(message);
	}

	@Override
	public void refreshColumn() {
		this.frame.displayDataInfo();
	}

	@Override
	public void setSelectedRobot(ColorRobotEnum c) {
		this.gameManager.setSelectedRobot(c);
		this.frame.displayBoard();
	}

	@Override
	public void loadPreviousPosition() {
		this.gameManager.loadPreviousPosition();
	}

	@Override
	public void loadNextPosition() {
		this.gameManager.loadNextPosition();
	}

	@Override
	public void startNewGame() {
		this.gameManager.startNewGame();
		this.gameManager.refreshScreen();
	}

	@Override
	public void displayHelp() {
		this.frame.displayHelp();

	}

	@Override
	public void startSolver() {
		this.gameManager.startSolver();
		this.frame.setFocusOnBoard();
	}

	@Override
	public void handleMouseAction(int line, int column) {
		this.gameManager.handleMouseAction(line, column);
	}

}
