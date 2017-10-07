package fr.amou.perso.app.rasen.robot.test;

import java.util.ArrayList;

import org.junit.Test;

import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;
import fr.amou.perso.app.rasen.robot.game.Robot;
import junit.framework.TestCase;

public class RobotTest extends TestCase {

    @Test
    public void testPlaceOnBoard() {

        Robot r = new Robot(Color.Blue);
        Robot r2 = new Robot(Color.Green);
        ArrayList<Robot> robots = new ArrayList<>();

        r.placeOnBoard(robots);
        robots.add(r);

        r2.placeOnBoard(robots);

        assertEquals(false, r.equals(r2));
    }

    @Test
    public void testRobotIsHere() {
        Robot r = new Robot(2, 4, Color.Blue);
        Robot r2 = new Robot(1, 4, Color.Green);
        ArrayList<Robot> robots = new ArrayList<>();
        robots.add(r);

        assertEquals(true, r2.robotIsHere(robots, Direction.Right));
        r.x = 0;

        assertEquals(true, r2.robotIsHere(robots, Direction.Left));

        r.x = 1;
        r.y = 5;

        assertEquals(true, r2.robotIsHere(robots, Direction.Down));

        r.y = 3;

        assertEquals(true, r2.robotIsHere(robots, Direction.Up));
        assertEquals(false, r2.robotIsHere(robots, Direction.Down));
    }

    @Test
    public void testEquals() {
        Robot r1 = new Robot(0, 0, Color.Blue);
        Robot r2 = new Robot(1, 1, Color.Green);

        assertEquals(false, r1.equals(r2));
        assertEquals(true, r1.equals(r1));
    }

    @Test
    public void testPosition() {
        Robot r1 = new Robot(0, 0, Color.Green, 0, 1);
        assertEquals(Color.Green, r1.getColor());
        assertEquals(0, r1.x);
        assertEquals(0, r1.y);
        assertEquals(0, r1.originX);
        assertEquals(1, r1.originY);

        Robot r2 = new Robot(r1);

        assertEquals(Color.Green, r2.getColor());
        assertEquals(Color.Green, r2.getColor());
        assertEquals(0, r2.x);
        assertEquals(0, r2.y);
        assertEquals(0, r2.originX);
        assertEquals(1, r2.originY);

        r2.setColor(Color.Red);
        assertEquals(Color.Red, r2.getColor());
        r2.setX(8);
        assertEquals(8, r2.x);
        r2.setY(9);
        assertEquals(9, r2.y);
        r2.setOriginX(5);
        assertEquals(5, r2.originX);
        r2.setOriginY(10);
        assertEquals(10, r2.originY);
    }
}
