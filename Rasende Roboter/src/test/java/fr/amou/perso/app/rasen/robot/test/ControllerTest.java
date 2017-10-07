package fr.amou.perso.app.rasen.robot.test;

import org.junit.Test;

import fr.amou.perso.app.rasen.robot.controller.Controller;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;
import junit.framework.TestCase;

public class ControllerTest extends TestCase {

    @Test
    public void testMoveRobotInDirection() {
        Controller c = new Controller();
        c.setSelectedRobot(Color.Green);

        int oldX = c.getgame().getSelectedRobot().x;

        c.moveRobotInDirection(Direction.Right);

        assertNotSame(oldX, c.getgame().getSelectedRobot().x);
    }

}
