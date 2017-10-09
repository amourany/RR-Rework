package fr.amou.perso.app.rasen.robot.game.manager;

import java.util.ArrayList;
import java.util.List;
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
        this.gameModel.setSelectedRobotByColor(c);
    }

    @Override
    public void handleMouseAction(int line, int column) {
        for (Robot r : this.gameModel.getRobotList()) {
            if (column == r.x && line == r.y) {
                this.gameModel.setSelectedRobot(r);
                this.frame.displayBoard();
                return;
            }
        }

        if (this.gameModel.getSelectedRobot() != null) {
            if (this.gameModel.getSelectedRobot().x == column) {
                if (this.gameModel.getSelectedRobot().y > line) {
                    this.moveRobotInDirection(DirectionDeplacementEnum.Up);
                } else {
                    this.moveRobotInDirection(DirectionDeplacementEnum.Down);
                }
            } else if (this.gameModel.getSelectedRobot().y == line) {
                if (this.gameModel.getSelectedRobot().x > column) {
                    this.moveRobotInDirection(DirectionDeplacementEnum.Left);
                } else {
                    this.moveRobotInDirection(DirectionDeplacementEnum.Right);
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
        // this.gameModel = new GameModel();

        this.gameModel.getBoard().initBoard();
        this.gameModel.getBoard().setBoard();

        this.placeRobots();

        // Création d'un tableau qui contient toutes les cartes possibles
        Stack<Box> goalCardsStack = new Stack<>();
        int i = 0;
        Box[] goalCardTab = new Box[17];
        for (ColorRobotEnum c : ColorRobotEnum.values()) {
            for (BoxTypeEnum bt : BoxTypeEnum.values()) {
                if (bt != BoxTypeEnum.Central && bt != BoxTypeEnum.Empty
                        && bt != BoxTypeEnum.Multi) {
                    goalCardTab[i] = new Box(bt, c);
                    i++;
                }
            }
        }
        goalCardTab[i] = new Box(BoxTypeEnum.Multi, null);

        // Ajout al�atoire des cartes dans la pile
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

        Board board = this.gameModel.getBoard();

        for (ColorRobotEnum c : ColorRobotEnum.values()) {
            Robot rob = new Robot(c);
            rob.placeOnBoard(robotList);
            while (board.getBox(rob.originY, rob.originX).getType() != BoxTypeEnum.Empty) {
                rob.placeOnBoard(robotList);
            }
            robotList.add(rob);
        }

        this.gameModel.setRobotList(robotList);
    }

    public void startNewLap() {

        Stack<Box> goalCardsStack = this.gameModel.getGoalCardsStack();

        if (goalCardsStack.empty()) {
            System.out.println("End of game !");
            this.gameModel.setOver(true);
        } else {

            for (Robot r : this.gameModel.getRobotList()) {
                r.newOrigin();
            }

            this.gameModel.setPreviousPositionStack(new Stack<>());
            this.gameModel.setNextPositionStack(new Stack<>());
            this.gameModel.setCurrentGoal(goalCardsStack.pop());
        }
    }

    /**
     * Save the current position of the robots, save only the position if it is
     * different from the last
     */
    private void saveCurrentPosition() {
        if (!this.gameModel.getPreviousPositionStack().empty()) {
            if (this.gameModel.getPreviousPositionStack().peek().equals(this.gameModel
                    .getRobotList())) {
                return;
            }
        }

        List<Robot> robots = new ArrayList<>();
        for (Robot r : this.gameModel.getRobotList()) {
            robots.add(new Robot(r));
        }

        this.gameModel.getNextPositionStack().removeAllElements();
        this.gameModel.getPreviousPositionStack().push(robots);
    }

    /**
     * Load the previous position
     */
    @Override
    public void loadPreviousPosition() {
        List<Robot> robots = new ArrayList<>();
        for (Robot r : this.gameModel.getRobotList()) {
            robots.add(new Robot(r));
        }
        this.gameModel.getNextPositionStack().push(robots);
        this.gameModel.setRobotList(this.gameModel.getPreviousPositionStack().pop());
        this.frame.display();
    }

    /**
     * Load the next position
     */
    @Override
    public void loadNextPosition() {
        List<Robot> robots = new ArrayList<>();
        for (Robot r : this.gameModel.getRobotList()) {
            robots.add(new Robot(r));
        }
        this.gameModel.getPreviousPositionStack().push(robots);
        this.gameModel.setRobotList(this.gameModel.getNextPositionStack().pop());
        this.frame.display();
    }

    /**
     * Move a robot in a direction
     *
     * @param robot
     *            : the robot to move
     * @param direction
     *            : Left, Right, Down, Up
     * @see DirectionDeplacementEnum
     * @see Robot
     */
    public void moveSelectedRobot(final DirectionDeplacementEnum dir) {
        this.saveCurrentPosition();

        this.gameModel.getBoard().getNewPosition(this.gameModel.getSelectedRobot(), dir,
                this.gameModel.getRobotList());
        if (this.gameModel.isWin(this.gameModel.getSelectedRobot())) {
            this.startNewLap();
        }
    }

}
