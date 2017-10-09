package fr.amou.perso.app.rasen.robot.game.data;

import java.util.List;
import java.util.Stack;

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
    private List<Robot> robotList;
    private Stack<List<Robot>> previousPositionStack;
    private Stack<List<Robot>> nextPositionStack;
    private Robot selectedRobot;
    private Stack<Box> goalCardsStack;
    private Box currentGoal;
    private boolean isOver;
    private String theme = "default/";

    public GameModel() {
        this.isOver = false;
        this.board = new Board();
        this.goalCardsStack = new Stack<>();
    }

    /**
     * Return the robot with the specified Color
     *
     * @param c
     *            : Color, the color of the robot asked
     * @return r : Robot, if the robot with this color exist, else null
     * @see ColorRobotEnum
     * @see Robot
     */
    public Robot getRobot(final ColorRobotEnum col) {
        Robot result = null;

        for (Robot r : this.robotList) {
            if (r.getColor() == col) {
                result = r;
            }
        }
        return result;
    }

    /**
     * Getter of Current Round
     *
     * @return currentRound : int
     */
    public int getCurrentRound() {
        return 17 - this.goalCardsStack.size();
    }

    public void setRobotByColor(final ColorRobotEnum color, final Robot rob) {
        for (int i = 0; i < this.robotList.size(); i++) {
            if (this.robotList.get(i).getColor() == color) {
                this.robotList.set(i, rob);
            }
        }
    }

    public void setSelectedRobotByColor(final ColorRobotEnum color) {
        for (Robot r : this.robotList) {
            if (r.getColor() == color) {
                this.selectedRobot = r;
            }
        }
    }

    public int getCounterLap() {
        return this.previousPositionStack.size();
    }

    public void initStack() {
        while (!this.goalCardsStack.isEmpty()) {
            this.goalCardsStack.pop();
        }
    }

    public Boolean hasPreviousPosition() {
        return !this.previousPositionStack.empty();
    }

    public Boolean hasNextPosition() {
        return !this.nextPositionStack.empty();
    }

    public boolean isWin(final Robot rob) {
        boolean win = false;

        Box currentGoal = this.getCurrentGoal();

        if (rob.getColor() == currentGoal.getColor()) {
            if (this.getBoard().getGameBoard()[rob.y][rob.x].getType() == currentGoal.getType()
                    && this.getBoard().getGameBoard()[rob.y][rob.x].getColor() == currentGoal
                            .getColor()) {
                win = true;
            }
        } else {
            if (this.getBoard().getGameBoard()[rob.y][rob.x].getType() == currentGoal.getType()
                    && currentGoal.getType() == BoxTypeEnum.Multi) {
                win = true;
            }
        }
        return win;
    }

}
