package fr.amou.perso.app.rasen.robot.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.amou.perso.app.rasen.robot.game.Constant;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;
import fr.amou.perso.app.rasen.robot.game.Game;
import fr.amou.perso.app.rasen.robot.game.Robot;
import lombok.Data;

@Data
@Component
public class Solver {

    @Autowired
    private StructTree tree;

    private String root;
    private String leaf;
    private Game game;
    private int depth;
    private int cut;
    private boolean solved;

    public void initSolver(Game g) {
        this.root = "";
        this.leaf = "";
        this.game = g;
        this.tree.clear();
        this.depth = 0;
        this.cut = 0;
        this.solved = false;
    }

    public void solve(Game g) {
        this.initSolver(g);
        this.createRoot();

        while (!this.solved && this.depth < 13) {
            this.depth++;
            this.solved = this.buildPossibilities();
        }

        if (this.solved) {
            System.out.println("Solution found, number of moves : " + (this.depth - 1));
            this.tree.buildStack(this.leaf, this.root);
        } else {
            System.out.println("No solutions found, try to do some moves and try again");
        }
    }

    public void createRoot() {
        this.root = this.encodeKey(this.game.getRobots());
        this.tree.addPossibility(this.root, this.depth);
    }

    /**
     * This function create all the possible moves
     */
    private boolean buildPossibilities() {
        boolean moved;
        String toAdd;
        List<Robot> robots = new ArrayList<>();
        List<Robot> copyRobot = new ArrayList<>();
        Map<String, Integer> copyTree = new HashMap<>();

        copyTree = this.tree.getKeyFromValue(this.depth - 1);

        for (String s : copyTree.keySet()) {

            robots = this.decodeKey(s);

            for (Robot r : robots) {
                for (Direction d : Constant.Direction.values()) { // Then for each robot we move them in all directions
                    copyRobot.clear();
                    copyRobot.addAll(robots);
                    Robot rCopy = new Robot(r);

                    moved = this.game.getBoard().getNewPosition(rCopy, d, copyRobot);

                    for (int i = 0; i < copyRobot.size(); i++) {
                        if (copyRobot.get(i).getColor() == rCopy.getColor()) {
                            copyRobot.set(i, rCopy);
                        }
                    }
                    if (moved) { // And then we add the key made by the move of each robot to the hashmap
                        if (!this.solved) {
                            if (this.game.isWin(r)) {
                                toAdd = this.encodeKey(copyRobot);
                                if (!this.tree.containsKey(toAdd)) {
                                    this.leaf = toAdd;
                                    this.tree.addParent(toAdd, s);
                                    this.solved = true;
                                }
                            } else {
                                toAdd = this.encodeKey(copyRobot);
                                if (!this.tree.containsKey(toAdd)) {
                                    this.tree.addPossibility(toAdd, this.depth);
                                    this.tree.addParent(toAdd, s);
                                } else {
                                    this.cut++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return this.solved;
    }

    private String encodeKey(List<Robot> robots) {
        String s = "";
        for (Robot r : robots) {
            s += r.toString() + "&";
        }
        return s;
    }

    private List<Robot> decodeKey(String key) {
        String subKey[];
        String robotInfo[];
        Robot rob;
        List<Robot> robots = new ArrayList<>();

        subKey = key.split("&");

        for (int i = 0; i < Constant.NB_ROBOT; i++) {
            robotInfo = subKey[i].split(";"); // We split the keys in order to get the four robots
            rob = new Robot(Integer.parseInt(robotInfo[0]), Integer.parseInt(robotInfo[1]), Constant.Color.valueOf(
                    robotInfo[2]));
            robots.add(rob);
        }

        return robots;
    }
}
