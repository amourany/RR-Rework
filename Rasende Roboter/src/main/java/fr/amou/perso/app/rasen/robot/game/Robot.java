package fr.amou.perso.app.rasen.robot.game;

import java.util.List;

import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import lombok.Data;

/**
 * Class containing the data of a robot (positions, color)
 */
@Data
public class Robot {
    public int x;
    public int y;
    public int originX;
    public int originY;
    /**
     * @see ColorRobotEnum
     */
    private ColorRobotEnum color;

    public Robot(final ColorRobotEnum col) {
        this.color = col;
    }

    public Robot(final int x, final int y, final ColorRobotEnum color) {
        this.x = x;
        this.y = y;
        this.originX = x;
        this.originY = y;
        this.color = color;
    }

    public Robot(final Robot rob) {
        this(rob.x, rob.y, rob.color);
        this.originX = rob.originX;
        this.originY = rob.originY;

    }

    public Robot(final int x, final int y, final ColorRobotEnum c, final int originX, final int originY) {
        this(x, y, c);
        this.originX = originX;
        this.originY = originY;
    }

    /**
     * Place a new Robot on the board
     *
     * @param robots
     *            : List<Robot>, set of the robots already on the board, use to know
     *            if the new robot is alone on the box
     */
    public void placeOnBoard(final List<Robot> robots) {
        boolean isAlone; // Is the robot alone on the box ?

        do {
            isAlone = true;
            do {
                this.x = (int) (Math.random() * (15 + 1)); // robots positions are between box 0 and box 15 without
                                                           // boxes 7
            } while (this.x == 7 || this.x == 8);

            do {
                this.y = (int) (Math.random() * (15 + 1));
            } while (this.x == 7 || this.x == 8);

            for (Robot r : robots) {
                if ((this.x == r.x) && (this.y == r.y)) {
                    isAlone = false;
                }
            }

        } while (!isAlone);

        this.originX = this.x;
        this.originY = this.y;
    }

    public boolean robotIsHere(final List<Robot> robots, final DirectionDeplacementEnum dir) {
        boolean res = false;

        for (Robot r : robots) {
            switch (dir) {
            case Right:
                if (r.getX() == this.x + 1 && r.getY() == this.y) {
                    res = true;
                }
                break;
            case Left:
                if (r.getX() == this.x - 1 && r.getY() == this.y) {
                    res = true;
                }
                break;
            case Down:
                if (r.getY() == this.y + 1 && r.getX() == this.x) {
                    res = true;
                }
                break;
            case Up:
                if (r.getY() == this.y - 1 && r.getX() == this.x) {
                    res = true;
                }
                break;
            default:
                break;
            }
        }
        return res;
    }

    @Override
    public boolean equals(Object r) {
        boolean res = true;
        if (this.color != ((Robot) r).getColor() || this.x != ((Robot) r).x || this.y != ((Robot) r).y) {
            res = false;
        }
        return res;
    }

    @Override
    public String toString() {
        return this.x + ";" + this.y + ";" + this.color;
    }

    public void newOrigin() {
        this.originX = this.x;
        this.originY = this.y;
    }
}
