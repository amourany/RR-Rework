package fr.amou.perso.app.rasen.robot.game.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

import fr.amou.perso.app.rasen.robot.enums.BoxTypeEnum;
import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.game.Board;
import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Robot;
import lombok.Data;

/**
 * The model of our architecture
 */
@Data
@Component
public class GameModel {

	private Board board;
	private Map<ColorRobotEnum, Robot> robotMap;
	private Deque<Entry<ColorRobotEnum, Robot>> previousPositionStack;
	private Deque<Entry<ColorRobotEnum, Robot>> nextPositionStack;
	private ColorRobotEnum selectedRobot;
	private Deque<Box> goalCardsStack;
	private Box currentGoal;
	private boolean isOver;

	public GameModel() {
		this.isOver = false;
		this.board = new Board();
		this.goalCardsStack = new ArrayDeque<>();
		this.robotMap = new HashMap<>();
	}

	/**
	 * Récupère un robot par sa couleur.
	 *
	 * @param col
	 * @return
	 */
	public Robot getRobotByColor(final ColorRobotEnum col) {
		return this.robotMap.get(col);
	}

	public Robot getCurrentRobot() {
		return this.robotMap.get(this.selectedRobot);
	}

	/**
	 * Getter of Current Round
	 *
	 * @return currentRound : int
	 */
	public int getCurrentRound() {
		return 17 - this.goalCardsStack.size();
	}

	public void setRobotByColor(Entry<ColorRobotEnum, Robot> entry) {
		this.robotMap.put(entry.getKey(), entry.getValue());
	}

	public void setRobotByColor(final ColorRobotEnum color, final Robot robot) {
		this.robotMap.put(color, robot);
	}

	public int getCounterLap() {
		return this.previousPositionStack.size();
	}

	public void initStack() {
		this.goalCardsStack.clear();
	}

	public Boolean hasPreviousPosition() {
		return !this.previousPositionStack.isEmpty();
	}

	public Boolean hasNextPosition() {
		return !this.nextPositionStack.isEmpty();
	}

	public boolean isWin(final Robot rob) {
		boolean win = false;

		Box currentGoal = this.getCurrentGoal();

		if (rob.getColor() == currentGoal.getColor()) {
			if (this.getBoard().getGameBoard()[rob.y][rob.x].getType() == currentGoal.getType()
					&& this.getBoard().getGameBoard()[rob.y][rob.x].getColor() == currentGoal.getColor()) {
				win = true;
			}
		} else {
			if (this.getBoard().getGameBoard()[rob.y][rob.x].getType() == currentGoal.getType()
					&& currentGoal.getType() == BoxTypeEnum.MULTI) {
				win = true;
			}
		}
		return win;
	}

	@Deprecated
	public List<Robot> getRobotPositionList() {

		return new ArrayList<>(this.robotMap.values());
	}

}
