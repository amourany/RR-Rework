package fr.amou.perso.app.rasen.robot.game.manager;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import fr.amou.perso.app.rasen.robot.enums.BoxTypeEnum;
import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Robot;
import fr.amou.perso.app.rasen.robot.game.data.GameModel;
import fr.amou.perso.app.rasen.robot.io.MessageWriter;
import fr.amou.perso.app.rasen.robot.solver.Solver;
import fr.amou.perso.app.rasen.robot.userInterface.RasendeViewInterface;

@Service
public class GameDefaultManager implements GameManager {

	@Autowired
	private GameModel gameModel;

	@Autowired
	private RasendeViewInterface frame;

	@Autowired
	private BoardManager boardManager;

	@Autowired
	private Random randomGenerator;

	@Autowired
	private MessageWriter messageWriter;

	@Override
	public void run() {
		this.startNewGame();
		this.frame.buildFrame();
		this.refreshScreen();
	}

	@Override
	public void moveRobotInDirection(final DirectionDeplacementEnum dir) {

		if (this.gameModel.getSelectedRobot() != null) {

			this.moveSelectedRobot(dir);

			this.frame.displayBoard();
			this.frame.displayDataInfo();

			if (this.gameModel.getIsOver()) {
				this.gameOver();
			}
		}
	}

	@Override
	public void gameOver() {
		this.frame.displayWin();
		this.startNewGame();
		this.refreshScreen();
	}

	@Override
	public void setSelectedRobot(ColorRobotEnum c) {
		this.gameModel.setSelectedRobot(c);
	}

	@Override
	public void handleMouseAction(int line, int column) {
		Map<ColorRobotEnum, Robot> robotMap = this.gameModel.getRobotMap();

		List<Entry<ColorRobotEnum, Robot>> selectedRobotEntryList = robotMap.entrySet().stream().filter(entry -> {
			Robot robot = entry.getValue();
			return column == robot.x && line == robot.y;
		}).collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(selectedRobotEntryList)) {
			Entry<ColorRobotEnum, Robot> selectedRobotEntry = selectedRobotEntryList.get(0);

			this.gameModel.setSelectedRobot(selectedRobotEntry.getKey());
		}

		Robot robot = this.gameModel.getCurrentRobot();

		DirectionDeplacementEnum direction = null;

		if (robot.x == column) {
			if (robot.y > line) {
				direction = DirectionDeplacementEnum.UP;
			} else if (robot.y < line) {
				direction = DirectionDeplacementEnum.DOWN;
			}
		} else if (robot.y == line) {
			if (robot.x > column) {
				direction = DirectionDeplacementEnum.LEFT;
			} else if (robot.x < column) {
				direction = DirectionDeplacementEnum.RIGHT;
			}
		}

		if (direction != null) {
			this.moveRobotInDirection(direction);
		}

		this.frame.displayBoard();

	}

	@Override
	public void startSolver() {
		Solver solver = new Solver(this.gameModel);
		String result = solver.solve();
		this.messageWriter.ecrireMessage(result);
	}

	@Override
	public void startNewGame() {

		this.boardManager.initialiserPlateau();

		this.boardManager.placerRobots();

		this.genererObjectifs();

		this.startNewLap();
	}

	private void genererObjectifs() {
		// Création d'un tableau qui contient toutes les cartes possibles
		List<BoxTypeEnum> typeInterdits = Arrays.asList(BoxTypeEnum.CENTRAL, BoxTypeEnum.EMPTY, BoxTypeEnum.MULTI);

		List<Box> goalCardList = new ArrayList<>();
		for (ColorRobotEnum c : ColorRobotEnum.values()) {
			for (BoxTypeEnum bt : BoxTypeEnum.values()) {
				if (!typeInterdits.contains(bt)) {
					goalCardList.add(new Box(bt, c));
				}
			}
		}

		goalCardList.add(new Box(BoxTypeEnum.MULTI, null));

		Collections.shuffle(goalCardList, this.randomGenerator);
		Deque<Box> goalCardsStack = new ArrayDeque<>();
		goalCardsStack.addAll(goalCardList);

		this.gameModel.setGoalCardsStack(goalCardsStack);
	}

	public void startNewLap() {

		Deque<Box> goalCardsStack = this.gameModel.getGoalCardsStack();

		if (goalCardsStack.isEmpty()) {
			this.messageWriter.ecrireMessage("End of game !");
			this.gameModel.setIsOver(true);
		} else {

			Map<ColorRobotEnum, Robot> robotMap = this.gameModel.getRobotMap();

			for (Entry<ColorRobotEnum, Robot> entry : robotMap.entrySet()) {
				Robot r = entry.getValue();
				r.newOrigin();
			}

			this.gameModel.setPreviousPositionStack(new ArrayDeque<>());
			this.gameModel.setNextPositionStack(new ArrayDeque<>());
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
		if (!this.gameModel.getPreviousPositionStack().isEmpty()) {
			if (this.gameModel.getPreviousPositionStack().peek().equals(entry)) {
				return;
			}
		}

		this.gameModel.getNextPositionStack().clear();
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

		this.refreshScreen();
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
		this.refreshScreen();
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
		Boolean win = this.gameModel.isWin(robot);

		if (win) {
			this.startNewLap();
		}
	}

	@Override
	public void refreshScreen() {
		this.frame.display();
	}

}
