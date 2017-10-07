package test;

import org.junit.Test;

import controller.Controller;
import game.Constant.Color;
import game.Constant.Direction;
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
