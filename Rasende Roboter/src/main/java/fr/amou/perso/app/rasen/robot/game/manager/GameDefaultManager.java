package fr.amou.perso.app.rasen.robot.game.manager;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.amou.perso.app.rasen.robot.enums.BoxTypeEnum;
import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.game.Board;
import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Robot;
import fr.amou.perso.app.rasen.robot.game.data.GameModel;
import fr.amou.perso.app.rasen.robot.solver.Solver;
import fr.amou.perso.app.rasen.robot.userInterface.RasendeViewInterface;

@Service
public class GameDefaultManager implements GameManager {

	@Autowired
	private GameModel gameModel;

	@Autowired
	private RasendeViewInterface frame;

	@Autowired
	private Solver solver;
	
	@Autowired
	private BoardManager boardManager;

	@Override
	public void run() throws Exception {
		this.startNewGame();
		this.frame.buildFrame();
		this.frame.display();
	}

	@Override
	public void moveRobotInDirection(final DirectionDeplacementEnum dir) {

		if (this.gameModel.getSelectedRobot() != null) {

			this.moveSelectedRobot(dir);

			this.frame.displayBoard();
			this.frame.displayDataInfo();

			if (this.gameModel.isOver()) {
				this.gameOver();
			}
		}
	}

	@Override
	public void gameOver() {
		this.frame.displayWin();
		this.startNewGame();
		this.frame.display();
	}

	@Override
	public void setSelectedRobot(ColorRobotEnum c) {
		this.gameModel.setSelectedRobot(c);
	}

	@Override
	public void handleMouseAction(int line, int column) {
		Map<ColorRobotEnum, Robot> robotMap = this.gameModel.getRobotMap();

		for (Entry<ColorRobotEnum, Robot> entry : robotMap.entrySet()) {

			Robot robot = entry.getValue();
			ColorRobotEnum color = entry.getKey();

			if (column == robot.x && line == robot.y) {
				this.gameModel.setSelectedRobot(color);
				this.frame.displayBoard();
				return;
			}
		}

		ColorRobotEnum selectedRobot = this.gameModel.getSelectedRobot();
		if (selectedRobot != null) {

			Robot robot = robotMap.get(selectedRobot);

			if (robot.x == column) {
				if (robot.y > line) {
					this.moveRobotInDirection(DirectionDeplacementEnum.UP);
				} else {
					this.moveRobotInDirection(DirectionDeplacementEnum.DOWN);
				}
			} else if (robot.y == line) {
				if (robot.x > column) {
					this.moveRobotInDirection(DirectionDeplacementEnum.LEFT);
				} else {
					this.moveRobotInDirection(DirectionDeplacementEnum.RIGHT);
				}
			}
		}

	}

	@Override
	public void startSolver() {
		this.solver.solve(this.gameModel);
	}

	@Override
	public void startNewGame() {

		boardManager.initialiserPlateau();

		this.placeRobots();

		// Création d'un tableau qui contient toutes les cartes possibles
		Stack<Box> goalCardsStack = new Stack<>();
		int i = 0;
		Box[] goalCardTab = new Box[17];
		for (ColorRobotEnum c : ColorRobotEnum.values()) {
			for (BoxTypeEnum bt : BoxTypeEnum.values()) {
				if (bt != BoxTypeEnum.CENTRAL && bt != BoxTypeEnum.EMPTY && bt != BoxTypeEnum.MULTI) {
					goalCardTab[i] = new Box(bt, c);
					i++;
				}
			}
		}
		goalCardTab[i] = new Box(BoxTypeEnum.MULTI, null);

		// Ajout aléatoire des cartes dans la pile
		for (int j = 17; j > 0; j--) {
			int k = (int) (Math.random() * 100) % j;
			goalCardsStack.push(goalCardTab[k]);
			if (k != (j - 1)) {
				Box tmp = goalCardTab[j - 1];
				goalCardTab[j - 1] = goalCardTab[k];
				goalCardTab[k] = tmp;
			}
		}

		this.gameModel.setGoalCardsStack(goalCardsStack);

		this.startNewLap();
	}

	private void placeRobots() {
		List<Robot> robotList = new ArrayList<>();
		Map<ColorRobotEnum, Robot> robotMap = new HashMap<>();

		Board board = this.gameModel.getBoard();

		for (ColorRobotEnum c : ColorRobotEnum.values()) {
			Robot rob = new Robot(c);
			rob.placeOnBoard(robotList);
			while (board.getBox(rob.originY, rob.originX).getType() != BoxTypeEnum.EMPTY) {
				rob.placeOnBoard(robotList);
			}
			robotList.add(rob);

			robotMap.put(c, rob);
		}

		this.gameModel.setRobotMap(robotMap);
	}

	public void startNewLap() {

		Stack<Box> goalCardsStack = this.gameModel.getGoalCardsStack();

		if (goalCardsStack.empty()) {
			System.out.println("End of game !");
			this.gameModel.setOver(true);
		} else {

			Map<ColorRobotEnum, Robot> robotMap = this.gameModel.getRobotMap();

			for (Entry<ColorRobotEnum, Robot> entry : robotMap.entrySet()) {
				Robot r = entry.getValue();
				r.newOrigin();
			}

			this.gameModel.setPreviousPositionStack(new Stack<>());
			this.gameModel.setNextPositionStack(new Stack<>());
			this.gameModel.setCurrentGoal(goalCardsStack.pop());
		}
	}

	/**
	 * Enregistre la position du robot selectionné dans la stack des positions
	 * précédentes.
	 */
	private void saveCurrentPosition() {

		ColorRobotEnum selectedRobot = this.gameModel.getSelectedRobot();
		Robot robot = this.gameModel.getRobotByColor(selectedRobot);
		Robot robotCopy = new Robot(robot);

		SimpleEntry<ColorRobotEnum, Robot> entry = new SimpleEntry<>(selectedRobot, robotCopy);

		// Insertion dans la stack des positions uniquement si le robot à vraiment
		// bougé.
		if (!this.gameModel.getPreviousPositionStack().empty()) {
			if (this.gameModel.getPreviousPositionStack().peek().equals(entry)) {
				return;
			}
		}

		this.gameModel.getNextPositionStack().removeAllElements();
		this.gameModel.getPreviousPositionStack().push(entry);
	}

	/**
	 * Load the previous position
	 */
	@Override
	public void loadPreviousPosition() {

		// Récupération du robot qui va bouger.
		Entry<ColorRobotEnum, Robot> previousPos = this.gameModel.getPreviousPositionStack().pop();

		// Récupération de la position courante du robot qui va bouger.
		ColorRobotEnum previousSelectedColor = previousPos.getKey();
		Robot currentRobot = this.gameModel.getRobotByColor(previousSelectedColor);
		Robot robotCopy = new Robot(currentRobot);

		// Construction de l'entry de la position courante du robot que l'on va
		// déplacer.
		SimpleEntry<ColorRobotEnum, Robot> currentRobotEntry = new SimpleEntry<>(previousSelectedColor, robotCopy);
		this.gameModel.getNextPositionStack().push(currentRobotEntry);

		// Déplacement du robot à sa position précédente.
		this.gameModel.setRobotByColor(previousPos);

		this.frame.display();
	}

	/**
	 * Load the next position
	 */
	@Override
	public void loadNextPosition() {
		// Récupération du robot qui va bouger.
		Entry<ColorRobotEnum, Robot> nextPos = this.gameModel.getNextPositionStack().pop();

		// Récupération de la position courante du robot qui va bouger.
		ColorRobotEnum nextSelectedColor = nextPos.getKey();
		Robot currentRobot = this.gameModel.getRobotByColor(nextSelectedColor);
		Robot robotCopy = new Robot(currentRobot);

		// Construction de l'entry de la position courante du robot que l'on va
		// déplacer.
		SimpleEntry<ColorRobotEnum, Robot> currentRobotEntry = new SimpleEntry<>(nextSelectedColor, robotCopy);
		this.gameModel.getPreviousPositionStack().push(currentRobotEntry);

		// Déplacement du robot à sa position suivante.
		this.gameModel.setRobotByColor(nextPos);
		this.frame.display();
	}

	/**
	 * Move a robot in a direction
	 *
	 * @param robot     : the robot to move
	 * @param direction : Left, Right, Down, Up
	 * @see DirectionDeplacementEnum
	 * @see Robot
	 */
	public void moveSelectedRobot(final DirectionDeplacementEnum dir) {
		this.saveCurrentPosition();

		List<Robot> robotList = this.gameModel.getRobotPositionList();
		Robot robot = this.gameModel.getCurrentRobot();

		this.gameModel.getBoard().getNewPosition(robot, dir, robotList);
		if (this.gameModel.isWin(robot)) {
			this.startNewLap();
		}
	}

}
