package fr.amou.perso.app.rasen.robot.solver;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import fr.amou.perso.app.rasen.robot.game.Constant;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;
import lombok.Data;

@Data
public class StructTree {
    public Map<String, Integer> possibilities;
    public Map<String, String> parent;
    public Stack<String> solutionStack;
    public String solution;

    public StructTree() {
        this.possibilities = new HashMap<>();
        this.parent = new HashMap<>();
        this.solutionStack = new Stack<>();
        this.solution = "";
    }

    public void clear() {
        this.possibilities.clear();
        this.parent.clear();
        this.solutionStack.clear();
        this.solution = "";
    }

    public boolean containsKey(String key) {
        return this.possibilities.containsKey(key);
    }

    public void addParent(String key, String keyParent) {
        this.parent.put(key, keyParent);
    }

    public void addPossibility(String key, int depth) {
        this.possibilities.put(key, depth);
    }

    public Map<String, Integer> getKeyFromValue(int depth) {
        Map<String, Integer> subMap = new HashMap<>();
        for (String key : this.possibilities.keySet()) {
            if (this.possibilities.get(key) == depth) {
                subMap.put(key, depth);
            }
        }

        return subMap;
    }

    public void buildStack(String leaf, String root) {

        String next;

        next = this.parent.get(leaf);
        this.solutionStack.push(next);

        while (!next.equals(root)) {
            next = this.parent.get(next);
            this.solutionStack.push(next);
        }

        String before = this.solutionStack.pop();
        String after;

        final int max = this.solutionStack.size();
        for (int i = 0; i < max; i++) {
            after = this.solutionStack.pop();
            this.solution += this.findDirection(before, after) + "\n";
            before = after;
        }

        System.out.println(this.solution);
    }

    private String findDirection(String after, String before) {
        String[] nodeBefore;
        String[] nodeAfter;
        String[] robotInfoBefore;
        String[] robotInfoAfter;
        String res = "Move the ";
        Direction d = null;

        nodeBefore = before.split("&");
        nodeAfter = after.split("&");

        for (int i = 0; i < Constant.NB_ROBOT; i++) {
            if (!nodeBefore[i].equals(nodeAfter[i])) {
                robotInfoBefore = nodeBefore[i].split(";");
                robotInfoAfter = nodeAfter[i].split(";");

                res += Constant.Color.valueOf(robotInfoBefore[2]) + " robot in the ";

                if (Integer.parseInt(robotInfoBefore[0]) > Integer.parseInt(robotInfoAfter[0])) {
                    d = Direction.Right;
                } else if (Integer.parseInt(robotInfoBefore[0]) < Integer.parseInt(robotInfoAfter[0])) {
                    d = Direction.Left;
                } else if (Integer.parseInt(robotInfoBefore[1]) > Integer.parseInt(robotInfoAfter[1])) {
                    d = Direction.Down;
                } else if (Integer.parseInt(robotInfoBefore[1]) < Integer.parseInt(robotInfoAfter[1])) {
                    d = Direction.Up;
                }
            }
        }
        res += d + " direction.";
        return res;
    }

}
