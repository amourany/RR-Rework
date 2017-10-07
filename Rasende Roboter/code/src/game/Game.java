package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import controller.Controller;
import game.Constant.BoxType;
import game.Constant.Color;
import game.Constant.Direction;
import solver.Solver;

/**
 * The model of our architecture
 */
public class Game {
    private Board mBoard;
    private List<Robot> mRobots;
    private Stack<List<Robot>> mPreviousPosition;
    private Stack<List<Robot>> mNextPosition;
    private Robot mSelectedRobot;
    private Stack<Box> mGoalCards;
    private Box currentGoal;
    private Solver mSolver;
    private boolean isOver;
    private String theme = "default/";

    public Game(Controller controller) {
        this.mSolver = new Solver();

        this.startNewGame();
    }

    public void startNewGame() {
        this.generateBoard();
        this.isOver = false;
        this.placeRobots();
        this.mGoalCards = new Stack<>();

        // Cr�ation d'un tableau qui contient toutes les cartes possibles
        int i = 0;
        Box[] goalCardTab = new Box[17];
        for (Color c : Color.values()) {
            for (BoxType bt : BoxType.values()) {
                if (bt != BoxType.Central && bt != BoxType.Empty && bt != BoxType.Multi) {
                    goalCardTab[i] = new Box(bt, c);
                    i++;
                }
            }
        }
        goalCardTab[i] = new Box(BoxType.Multi, null);

        // Ajout al�atoire des cartes dans la pile
        for (int j = 17; j > 0; j--) {
            int k = (int) (Math.random() * 100) % j;
            this.mGoalCards.push(goalCardTab[k]);
            if (k != (j - 1)) {
                Box tmp = goalCardTab[j - 1];
                goalCardTab[j - 1] = goalCardTab[k];
                goalCardTab[k] = tmp;
            }
        }
        this.startNewLap();
    }

    private void placeRobots() {
        this.mRobots = new ArrayList<>();
        Robot rob;

        for (Color c : Constant.Color.values()) {
            rob = new Robot(c);
            rob.placeOnBoard(this.mRobots);
            while (this.mBoard.getBox(rob.originY, rob.originX).getType() != Constant.BoxType.Empty) {
                rob.placeOnBoard(this.mRobots);
            }
            this.mRobots.add(rob);
        }
    }

    /**
     * Generates a new Board
     *
     */
    private void generateBoard() {
        this.mBoard = new Board();
        this.mBoard.initBoard();
        this.mBoard.setBoard();

    }

    public void startNewLap() {
        if (this.mGoalCards.empty()) {
            System.out.println("End of game !");
            this.isOver = true;
        } else {

            for (Robot r : this.mRobots) {
                r.newOrigin();
            }

            this.mPreviousPosition = new Stack<>();
            this.mNextPosition = new Stack<>();
            this.currentGoal = this.mGoalCards.pop();
        }
    }

    /**
     * Getter of gameBoard
     *
     * @return gameBoard : Board
     * @see Board
     */
    public Board getBoard() {
        return this.mBoard;
    }

    /**
     * Getter of the robots' list
     *
     * @return robots : the list of robots
     * @see Robot
     * @see List
     */
    public List<Robot> getRobots() {
        return this.mRobots;
    }

    /**
     * Return the robot with the specified Color
     *
     * @param c
     *            : Color, the color of the robot asked
     * @return r : Robot, if the robot with this color exist, else null
     * @see Color
     * @see Robot
     */
    public Robot getRobot(final Color col) {
        Robot result = null;

        for (Robot r : this.mRobots) {
            if (r.getColor() == col) {
                result = r;
            }
        }
        return result;
    }

    /**
     * Setter of the robots' list
     *
     * @param robots
     * @see Robot
     * @see List
     */
    public void setRobots(final List<Robot> robots) {
        this.mRobots = robots;
    }

    /**
     * Getter of Current Round
     *
     * @return currentRound : int
     */
    public int getCurrentRound() {
        return 17 - this.mGoalCards.size();
    }

    public void setRobotByColor(final Color color, final Robot rob) {
        for (int i = 0; i < this.mRobots.size(); i++) {
            if (this.mRobots.get(i).getColor() == color) {
                this.mRobots.set(i, rob);
            }
        }
    }

    /**
     * Save the current position of the robots, save only the position if it is
     * different from the last
     */
    private void saveCurrentPosition() {
        if (!this.mPreviousPosition.empty()) {
            if (this.mPreviousPosition.peek().equals(this.mRobots)) {
                return;
            }
        }

        List<Robot> robots = new ArrayList<>();
        for (Robot r : this.mRobots) {
            robots.add(new Robot(r));
        }

        this.mNextPosition.removeAllElements();
        this.mPreviousPosition.push(robots);
    }

    /**
     * Load the previous position
     */
    public void loadPreviousPosition() {
        List<Robot> robots = new ArrayList<>();
        for (Robot r : this.mRobots) {
            robots.add(new Robot(r));
        }
        this.mNextPosition.push(robots);
        this.mRobots = this.mPreviousPosition.pop();
    }

    /**
     * Load the next position
     */
    public void loadNextPosition() {
        List<Robot> robots = new ArrayList<>();
        for (Robot r : this.mRobots) {
            robots.add(new Robot(r));
        }
        this.mPreviousPosition.push(robots);
        this.mRobots = this.mNextPosition.pop();
    }

    /**
     * Move a robot in a direction
     *
     * @param robot
     *            : the robot to move
     * @param direction
     *            : Left, Right, Down, Up
     * @see Direction
     * @see Robot
     */
    public void moveSelectedRobot(final Direction dir) {
        this.saveCurrentPosition();

        this.mBoard.getNewPosition(this.mSelectedRobot, dir, this.mRobots);
        if (this.isWin(this.mSelectedRobot)) {
            this.startNewLap();
        }
    }

    public boolean isWin(final Robot rob) {
        boolean win = false;

        if (rob.getColor() == this.currentGoal.getColor()) {
            if (this.mBoard.getGameBoard()[rob.y][rob.x].getType() == this.currentGoal.getType()
                    && this.mBoard.getGameBoard()[rob.y][rob.x].getColor() == this.currentGoal.getColor()) {
                win = true;
            }
        } else {
            if (this.mBoard.getGameBoard()[rob.y][rob.x].getType() == this.currentGoal.getType()
                    && this.currentGoal.getType() == BoxType.Multi) {
                win = true;
            }
        }
        return win;
    }

    public Robot getSelectedRobot() {
        return this.mSelectedRobot;
    }

    public void setSelectedRobot(final Robot selectedRobot) {
        this.mSelectedRobot = selectedRobot;
    }

    public void setSelectedRobot(final Color color) {
        for (Robot r : this.mRobots) {
            if (r.getColor() == color) {
                this.mSelectedRobot = r;
            }
        }
    }

    public int getCounterLap() {
        return this.mPreviousPosition.size();
    }

    public void startSolver() {
        this.mSolver.solve(this);
    }

    public Stack<Box> getStack() {
        return this.mGoalCards;
    }

    public void initStack() {
        while (!this.mGoalCards.isEmpty()) {
            this.mGoalCards.pop();
        }
    }

    public Box getCurrentGoal() {
        return this.currentGoal;
    }

    public void setCurrentGoal(Box goal) {
        this.currentGoal = goal;
    }

    public boolean isOver() {
        return this.isOver;
    }

    public Stack<Box> getmGoalCards() {
        return this.mGoalCards;
    }

    public void setmGoalCards(Stack<Box> mGoalCards) {
        this.mGoalCards = mGoalCards;
    }

    public String getTheme() {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean hasPreviousPosition() {
        return !this.mPreviousPosition.empty();
    }

    public Boolean hasNextPosition() {
        return !this.mNextPosition.empty();
    }

    public Stack<List<Robot>> getmPreviousPosition() {
        return this.mPreviousPosition;
    }

    public void setmPreviousPosition(Stack<List<Robot>> mPreviousPosition) {
        this.mPreviousPosition = mPreviousPosition;
    }

    public Stack<List<Robot>> getmNextPosition() {
        return this.mNextPosition;
    }

    public void setmNextPosition(Stack<List<Robot>> mNextPosition) {
        this.mNextPosition = mNextPosition;
    }
}
