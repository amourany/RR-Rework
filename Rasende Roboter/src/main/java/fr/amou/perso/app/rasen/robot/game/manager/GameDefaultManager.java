package fr.amou.perso.app.rasen.robot.game.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.game.Game;
import fr.amou.perso.app.rasen.robot.game.Robot;
import fr.amou.perso.app.rasen.robot.solver.Solver;
import fr.amou.perso.app.rasen.robot.userInterface.RasendeViewInterface;

@Service
public class GameDefaultManager implements GameManager {

    @Autowired
    private Game game;

    @Autowired
    private RasendeViewInterface frame;

    @Autowired
    private Solver solver;

    @Override
    public void run() throws Exception {
        this.game.startNewGame();
        this.frame.buildFrame();
    }

    @Override
    public void moveRobotInDirection(final DirectionDeplacementEnum dir) {

        if (this.game.getSelectedRobot() != null) {

            this.game.moveSelectedRobot(dir);

            this.frame.displayBoard();
            this.frame.displayDataInfo();

            if (this.game.isOver()) {
                this.gameOver();
            }
        }
    }

    @Override
    public void gameOver() {
        this.frame.displayWin();
        this.game.startNewGame();
        this.frame.display();
    }

    @Override
    public void setSelectedRobot(ColorRobotEnum c) {
        this.game.setSelectedRobot(c);
    }

    @Override
    public void handleMouseAction(int line, int column) {
        for (Robot r : this.game.getRobots()) {
            if (column == r.x && line == r.y) {
                this.game.setSelectedRobot(r);
                this.frame.displayBoard();
                return;
            }
        }

        if (this.game.getSelectedRobot() != null) {
            if (this.game.getSelectedRobot().x == column) {
                if (this.game.getSelectedRobot().y > line) {
                    this.moveRobotInDirection(DirectionDeplacementEnum.Up);
                } else {
                    this.moveRobotInDirection(DirectionDeplacementEnum.Down);
                }
            } else if (this.game.getSelectedRobot().y == line) {
                if (this.game.getSelectedRobot().x > column) {
                    this.moveRobotInDirection(DirectionDeplacementEnum.Left);
                } else {
                    this.moveRobotInDirection(DirectionDeplacementEnum.Right);
                }
            }
        }

    }

    @Override
    public void loadPreviousPosition() {
        this.game.loadPreviousPosition();
        this.frame.display();
    }

    @Override
    public void loadNextPosition() {
        this.game.loadNextPosition();
        this.frame.display();
    }

    @Override
    public void startNewGame() {
        this.game.startNewGame();
        this.frame.display();
    }

    @Override
    public void startSolver() {
        this.solver.solve();
    }
}
